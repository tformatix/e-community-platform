//------------------------------------------------------------------------------
// Author: Markus Flohberger
// E-Mail: Markus.Flohberger@energieag.at
// Changed: 2013-05-13 13:00h
//
// Author: Tobias Fischer, Michael Zauner (FH OÖ)
// E-Mail: tobias@tformatix.at
// Changed: 2022-05-16 13:00h
// 
// Based on File by Robert Johansson, Raditex AB (rSCADA)
//
// Example:
// make && ./mbus-slave -d /dev/ttyUSB0
//------------------------------------------------------------------------------

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <bitset>
#include <ctime>
#include <cstdlib>
#include <sqlite3.h>
#include <libconfig.h++>

#include <iostream>
#include <stdio.h>

extern "C" {
#include <mbus/mbus.h>
}

#include "auxfunctions.h"
#include "wmbus-encryption.h"
#include "err_codes.h"

using namespace std;
using namespace libconfig;

static int debug = 0; // Argument passed by command-line, Debug mode shows all messages on line

//#define WRITE_AMIS_DATA_TXT // Writes AMIS data to txt-File
#undef WRITE_AMIS_DATA_TXT

#define WRITE_AMIS_DATA_SQL  // Writes AMIS data to SQL database
//#undef WRITE_AMIS_DATA_SQL

#define PRINT_AMIS_DATA_STDOUT // Writes AMIS data to console
//#undef PRINT_AMIS_DATA_STDOUT

const char *FILENAME_DATABASE = "e-community-local.db";
const char *FILENAME_CONFIG = "config.ini";

const int BAUDRATE = 9600; // Baud rate for the connection

const int MAXIMUM_FRAME_LENGTH = 256; // in bytes
const int AES_KEY_LENGTH = 16; // in bytes
const int AES_KEY_STRING_LENGTH = 3 * AES_KEY_LENGTH - 1; // 2x16 half bytes and spaces
const int DEVICE_STRING_LENGTH = 14; // /dev/ttyUSBxx

static char aeskey[AES_KEY_STRING_LENGTH + 1];

#ifdef WRITE_AMIS_DATA_SQL

// Callback function for sqlite
static int callback(void *NotUsed, int argc, char **argv, char **azColName) {
    int i;
    for (i = 0; i < argc; i++) {
        printf("%s = %s\n", azColName[i], argv[i] ? argv[i] : "NULL");
    }
    printf("\n");
    return 0;
}

// Callback function for sqlite (AES Key)
static int callbackAESKey(void *NotUsed, int argc, char **argv, char **azColName) {
    if (argv[0] != nullptr) {
        strcpy(aeskey, argv[0]);
        printf("[%s] Loaded AES-Key: %s\n", currentDateTime(), aeskey);
    }
    return 0;
}

#endif

//------------------------------------------------------------------------------
// Execution starts here:
//------------------------------------------------------------------------------
int main(int argc, char **argv) {
    fprintf(stdout, "Time: %s\n", currentDateTime());

    int database_interval_rt = 0;
    int database_interval_hist = 0;
    int database_interval_counter = 0;
    char device1[DEVICE_STRING_LENGTH];



    //------------------------------------------------------------------------------
    // Read config file
    //------------------------------------------------------------------------------
    Config cfg;

    try {
        /* Load the configuration.. */
        printf("[%s] Loading config-file '%s' ...\n", currentDateTime(),
               FILENAME_CONFIG);
        cfg.readFile(FILENAME_CONFIG);
    } catch (const FileIOException &fioex) {
        printf("[%s] Error: ", currentDateTime());
        printf("I/O error while reading file.\n");
        return (EXIT_FAILURE);
    } catch (ParseException &pex) {
        printf("[%s] Error: ", currentDateTime());
        cout << "Parse error at " << FILENAME_CONFIG << ":" << pex.getLine()
             << " - " << pex.getError() << endl;
        return (EXIT_FAILURE);
    } catch (...) {
        printf("[%s] Error: ", currentDateTime());
        printf("Unknown cause!\n");
        return (EXIT_FAILURE);
    }

    try {
        string device1_string = cfg.lookup("device1");
        strcpy(device1, (char *) device1_string.c_str());
        printf("[%s] -- loaded device1: %s\n", currentDateTime(), device1);

        database_interval_rt = cfg.lookup("database_interval_rt");
        printf("[%s] -- loaded database_interval_rt: %d\n", currentDateTime(),
               database_interval_rt);

        database_interval_hist = cfg.lookup("database_interval_hist");
        printf("[%s] -- loaded database_interval_hist: %d\n", currentDateTime(),
               database_interval_hist);

        debug = cfg.lookup("debug_mode");
        printf("[%s] -- loaded debug_mode: %d\n", currentDateTime(), debug);

    } catch (const SettingNotFoundException &nfex) {
        printf("[%s] Error: ", currentDateTime());
        cout << "Required setting not in configuration file." << endl;
        return (EXIT_FAILURE);
    }
    printf("[%s] Loading config-file '%s' ... ok!\n", currentDateTime(),
           FILENAME_CONFIG);
    //------------------------------------------------------------------------------

    //------------------------------------------------------------------------------
    // Initialization of the database
    //------------------------------------------------------------------------------
#ifdef WRITE_AMIS_DATA_SQL
    unsigned int sql_entry_counter = 0;
    unsigned int sql_max_entries = 90;

    sqlite3 *database;
    char *sql_error_message_aes_key = 0;
    char *sql_error_message_rt = 0;
    char *sql_error_message_hist = 0;
    int database_return_code = 0;
    char record_string[500];
    char timestamp_string[19];

    database_return_code = sqlite3_open(FILENAME_DATABASE, &database);

    if (database_return_code != SQLITE_OK) {
        printf("[%s] ERROR: Can't open database: %s\n", currentDateTime(), sqlite3_errmsg(database));
        sqlite3_close(database);
        return (1);
    } else {
        printf("[%s] Opened database successfully\n", currentDateTime());
    }

    database_return_code = sqlite3_exec(database,
                                        "SELECT AESKey FROM SmartMeter;",
                                        callbackAESKey,
                                        0,
                                        &sql_error_message_aes_key);

    if (database_return_code != SQLITE_OK) {
        printf("[%s] SQL Error: %s\n", currentDateTime(), sqlite3_errmsg(database));
        sqlite3_close(database);
        return (1);
    }

    if (strlen(aeskey) == 0) {
        printf("[%s] ERROR: No AES-Key found!\n", currentDateTime());
        return 1;
    }

#endif
    //------------------------------------------------------------------------------

    //------------------------------------------------------------------------------
    // Initialization of debug output
    //------------------------------------------------------------------------------


    // Dump File for debug mode
    DUMP_FILE = fopen("dump_file.txt", "w");

    if (debug) {
        mbus_register_send_event(&mbus_dump_send_event);
        mbus_register_recv_event(&mbus_dump_recv_event);
    }

    //------------------------------------------------------------------------------

    // ***************************************
    // **** M-Bus Decryption Initialisation ****
    // ***************************************

    // Encryption Class from library
    WMBusEncryption *crypt;
    crypt = new WMBusEncryption(aeskey);

    // Data Frame for encryption library
    DATA_FRAME decrypt_frame;
    decrypt_frame.data = NULL;
    decrypt_frame.data = new byte[MAXIMUM_FRAME_LENGTH];

    mbus_data_variable_header receive_header = {'\0'};
    size_t mbus_data_variable_header_length = sizeof(mbus_data_variable_header);

    // error status for encryption library
    int status = WMBUS_ERR_NONE;

    // ***************************************

    //------------------------------------------------------------------------------
    // Initialization M-Bus connection
    //------------------------------------------------------------------------------
    mbus_handle *handle;
    mbus_frame master_frame;
    mbus_frame old_frame;
    int ret;

    if ((handle = mbus_connect_serial(device1)) == NULL) {
        printf("[%s] ERROR: Failed to setup connection to M-bus device1: %s\n",
               currentDateTime(),
               mbus_error_str());
        return 1;
    }

    if (mbus_serial_set_baudrate(handle->m_serial_handle, BAUDRATE) == -1) {
        printf("[%s] ERROR: Failed to set baud rate.\n", currentDateTime());
        return 1;
    }

    // int max_count = 100000; UNUSED
    int repeat_counter = 0;

    double time_difference = 0.0;
    time_t current_time = time(0);
    time_t old_database_time = time(0);

    // ********************************
    // **** Receive AMIS Data loop ****
    // ********************************
//  for (int count = 0; count <= max_count; count++)
    while (1) {
        int sleeptime = 10; // milliseconds

        // Looking for frames
        ret = mbus_recv_frame(handle, &master_frame);

//    printf("(%d/%d) Ret %d\n", count, max_count, ret);

        if (ret == -1) {
//      if (count%1000 == 0)
//        printf("(%d/%d) No message received: %s\n", count, max_count, mbus_error_str());

            usleep(sleeptime * 1000);
            continue;
        }

        if (ret == -2) {
            // printf("(%d/%d) Invalid message: The address address probably match more than one device1: %s\n", count, max_count, mbus_error_str());
            //      return 1;
            usleep(sleeptime * 1000);
            continue;
        }

        // Look if same frame (not 100% correct)
        if (ret == 0) {
            if (compare_mbus_frames(&master_frame, &old_frame)) {
                repeat_counter++;
                if (repeat_counter > 3) {
//          printf("(%d/%d) Ret %d, Same Frame\n", count, max_count, ret);
                    usleep(sleeptime * 1000);
                    continue;
                }
            } else {
//        printf("(%d/%d) Ret %d, Diff Frame\n", count, max_count, ret);
                set_mbus_frame(&master_frame, &old_frame);
                repeat_counter = 0;
            }
        }

        // *******************************************************************************************
        // Customer Interface new style
        // *******************************************************************************************

        // *******************************************************************************************
        // Short Frame: Master sends to primary address 240 (0xF0) reset command, after ACK, encrypted data will be pushed
        // e.g. 10 40 F0 30 16
        if (mbus_frame_type(&master_frame) == MBUS_FRAME_TYPE_SHORT) {
            // SND_NKE Frame
            if (((master_frame.control == MBUS_CONTROL_MASK_SND_NKE))
                && (master_frame.address == 0xF0)) {

                // Slave has to answer with ACK
                if (mbus_send_ack_frame(handle) == -1) {
                    printf("[%s] ERROR: Failed to send ACK: %s\n", currentDateTime(), mbus_error_str());
                    return 1;
                }

                printf("[%s] SND_NKE to primary address 240 received, ACK sent\n", currentDateTime());

            } // end: SND_NKE
        } // end: short frame
        // *******************************************************************************************

        // ************************************************
        // **** Encrypted AMIS data, IMA-Schnittstelle ****
        // ************************************************
        // Receive Long frame: Data
        if (mbus_frame_type(&master_frame) == MBUS_FRAME_TYPE_LONG) {
            if (((master_frame.control == MBUS_CONTROL_MASK_SND_UD)
                 || (master_frame.control
                     == (MBUS_CONTROL_MASK_SND_UD | MBUS_CONTROL_MASK_FCB)))
                && (master_frame.control_information
                    == MBUS_CONTROL_INFO_CMD_TO_DEVICE)) {

                if (master_frame.address != 0xF0) {
                    continue;
                }

                unsigned int frame_length = master_frame.data_size;
                unsigned int encrypted_data_length = frame_length
                                                     -
                                                     mbus_data_variable_header_length; // subtract header (checksum and stopbit already subtracted)

                if (mbus_send_ack_frame(handle) == -1) {
                    printf("[%s] ERROR: Failed to send ACK: %s\n",currentDateTime(), mbus_error_str());
                    return 1;
                }

                // copy data from mbuslib data structure to wmbuslib data structure
                // first copy the variable data fixed header (of mbuslib)
                if (master_frame.data_size < mbus_data_variable_header_length)
                    return -1;

                if ((encrypted_data_length % 16) != 0)
                    return WMBUS_ERR_SERIAL_PORT_READ_ERR;

                memcpy((void *) &(receive_header), (void *) (master_frame.data),
                       mbus_data_variable_header_length);

                // now copy to wmbuslib
                // ATTENTION: order of manufacturer bytes is changed, described in OMS!
                memcpy(decrypt_frame.sec_addr, receive_header.manufacturer, 2);
                memcpy(&decrypt_frame.sec_addr[2], receive_header.id_bcd, 4);
                memcpy(&decrypt_frame.sec_addr[6], &receive_header.version, 1);
                memcpy(&decrypt_frame.sec_addr[7], &receive_header.medium, 1);

                decrypt_frame.acc_nr = receive_header.access_no;
                decrypt_frame.status = receive_header.status;

                memcpy(decrypt_frame.signature, receive_header.signature, 2);

                decrypt_frame.c_field = master_frame.control;
                decrypt_frame.ci_field = master_frame.control_information;
                decrypt_frame.length = encrypted_data_length
                                       + FRAME_HEADER_SIZE;

                memcpy(decrypt_frame.data,
                       &master_frame.data[mbus_data_variable_header_length],
                       encrypted_data_length);

                // Decryption of data
                status = crypt->decrypt(&decrypt_frame, decrypt_frame.data,
                                        decrypt_frame.length - FRAME_HEADER_SIZE);
                //          printf("Decryption status: %d\n", status);

                if (status != WMBUS_ERR_NONE) {
                    if (status == WMBUS_ERR_ENCRYPTION_ERROR) {
                        printf("[%s] ERROR: Encryption failed, maybe wrong AES-Key?\n",
                               currentDateTime());
                        return 1;
                    } else {
                        return status;
                    }
                }

                // Extract AMIS data
                struct tm amis_data_time;
                extract_date_time(&amis_data_time, &decrypt_frame.data[4], 6);

                sprintf(timestamp_string, "%04d-%02d-%02d %02d:%02d:%02d",
                        (amis_data_time.tm_year + 2000),
                        (amis_data_time.tm_mon + 1), amis_data_time.tm_mday,
                        amis_data_time.tm_hour, amis_data_time.tm_min,
                        amis_data_time.tm_sec);

                int active_energy_plus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[12], 4);
                int active_energy_minus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[19], 4);
                int reactive_energy_plus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[28], 4);
                int reactive_energy_minus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[38], 4);
                int active_power_plus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[44], 4);
                int active_power_minus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[51], 4);
                int reactive_power_plus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[58], 4);
                int reactive_power_minus = (int) mbus_data_int_decode(
                        &decrypt_frame.data[66], 4);
                int prepayment_counter = (int) mbus_data_int_decode(
                        &decrypt_frame.data[74], 4);

                if (debug) {
                    printf("[%s] Decrypt: ", currentDateTime());
                    for (int count = 0;
                         count < decrypt_frame.length - FRAME_HEADER_SIZE;
                         count++) {
                        printf(" %02X", (u_char) decrypt_frame.data[count]);
                    } // end for loop: count
                    printf("\n");
                }

#ifdef PRINT_AMIS_DATA_STDOUT
                printf("[%s] AMIS Meter: %s %d %d %d %d %d %d %d %d %d\n",
                       currentDateTime(), timestamp_string, active_energy_plus,
                       active_energy_minus, reactive_energy_plus,
                       reactive_energy_minus, active_power_plus,
                       active_power_minus, reactive_power_plus,
                       reactive_power_minus, prepayment_counter);
#endif

#ifdef WRITE_AMIS_DATA_TXT
                fprintf(fp, "%s %d %d %d %d %d %d %d %d %d\n", timestamp_string, active_energy_plus, active_energy_minus, reactive_energy_plus, reactive_energy_minus, active_power_plus, active_power_minus, reactive_power_plus, reactive_power_minus, prepayment_counter);
                fflush(fp);
#endif

#ifdef WRITE_AMIS_DATA_SQL
                database_interval_counter++;
                current_time = time(0);
                time_difference = difftime(current_time, old_database_time);

                // WRITE IN REALTIME TABLE
                if (database_interval_counter % database_interval_rt == 0) {
                    database_interval_counter = 0;

                    // Check number of entries and delete in case
                    if (sql_entry_counter >= sql_max_entries) {
                        sprintf(record_string, "DELETE FROM MeterDataRealTime;");

                        // Execute SQL statement
                        database_return_code = sqlite3_exec(database,
                                                            record_string, callback, 0,
                                                            &sql_error_message_rt);
                        if (database_return_code != SQLITE_OK) {
                            fprintf(stderr, "SQL error (RT-502): %s\n",
                                    sql_error_message_rt);
                            sqlite3_free(sql_error_message_rt);
                        }
                        printf(
                                "[%s] Deleted old realtime entries (%d) from database %s.\n",
                                currentDateTime(), sql_entry_counter,
                                FILENAME_DATABASE);
                        sql_entry_counter = 0;
                    }

                    // Insert data to database
                    sprintf(record_string,
                            "INSERT INTO MeterDataRealTime (Id,Timestamp,ActiveEnergyPlus,ActiveEnergyMinus,ReactiveEnergyPlus,ReactiveEnergyMinus,ActivePowerPlus,ActivePowerMinus,ReactivePowerPlus,ReactivePowerMinus,PrepaymentCounter) VALUES (NULL,'%s' , %d, %d, %d, %d, %d, %d, %d, %d, %d );",
                            timestamp_string, active_energy_plus,
                            active_energy_minus, reactive_energy_plus,
                            reactive_energy_minus, active_power_plus,
                            active_power_minus, reactive_power_plus,
                            reactive_power_minus, prepayment_counter);

                    //          printf("[%s] AMIS Data written do database %s.\n", currentDateTime(), FILENAME_DATABASE);

                    // Execute SQL statement
                    database_return_code = sqlite3_exec(database, record_string,
                                                        callback, 0, &sql_error_message_rt);
                    if (database_return_code != SQLITE_OK) {
                        usleep(sleeptime * 1000);
                        database_return_code = sqlite3_exec(database, record_string,
                                                            callback, 0, &sql_error_message_rt);

                        if (database_return_code != SQLITE_OK) {
                            fprintf(stderr, "SQL error (RT-528): %s\n",
                                    sql_error_message_rt);
                            sqlite3_free(sql_error_message_rt);
                        }
                    }

                    sql_entry_counter++;
//					printf("[%s] Counter (%d/%d).\n", currentDateTime(), sql_entry_counter, sql_max_entries);
                }

                //Write in HIST TABLE
                if (((amis_data_time.tm_min == 0) || (amis_data_time.tm_min == 15) || (amis_data_time.tm_min == 30) ||
                     (amis_data_time.tm_min == 45)) && (amis_data_time.tm_sec == 00)) {
                    // Create SQL statement
                    sprintf(record_string,
                            "INSERT INTO MeterDataHistory (Id,Timestamp,ActiveEnergyPlus,ActiveEnergyMinus,ReactiveEnergyPlus,ReactiveEnergyMinus,ActivePowerPlus,ActivePowerMinus,ReactivePowerPlus,ReactivePowerMinus,PrepaymentCounter) VALUES (NULL,'%s' , %d, %d, %d, %d, %d, %d, %d, %d, %d );",
                            timestamp_string, active_energy_plus,
                            active_energy_minus, reactive_energy_plus,
                            reactive_energy_minus, active_power_plus,
                            active_power_minus, reactive_power_plus,
                            reactive_power_minus, prepayment_counter);

                    //          printf("[%s] AMIS Data written do database %s.\n", currentDateTime(), FILENAME_DATABASE);

                    // Execute SQL statement
                    database_return_code = sqlite3_exec(database, record_string,
                                                        callback, 0, &sql_error_message_hist);
                    if (database_return_code != SQLITE_OK) {
                        fprintf(stderr, "SQL error (Hist-554): %s\n",
                                sql_error_message_hist);
                        sqlite3_free(sql_error_message_hist);
                    }

                    old_database_time = current_time;
                }
                // END - write in HIST TABLE
#endif

            } // end if: encrypted data
            // ************************************************

        } // end: long frame
        // *******************************************************************************************

        // Wait for a short period
        usleep(sleeptime * 1000);

    } // end: AMIS receive loop
    // *******************************************************************************************

    if (decrypt_frame.data != NULL)
        delete[] decrypt_frame.data;

#ifdef WRITE_AMIS_DATA_TXT
    fclose(fp);
#endif
    mbus_disconnect(handle);

#ifdef WRITE_AMIS_DATA_SQL
    sqlite3_close(database);
#endif

    printf("Program %s terminated successfully\n", argv[0]);

    return 0;
}

