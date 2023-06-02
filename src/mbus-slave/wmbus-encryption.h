/** @mainpage
 *  \par Abstract
 *  This library contains the functionality to request information from an
 *  inteligent meter over <a href="http://www.m-bus.com/" target="_blank">wM-Bus</a> 
 *  and parse its response into an easily usable datastructure. In addition it provides
 *  a <a href="http://java.sun.com/docs/books/jni/html/jniTOC.html" target="_blank">JNI</a>
 *  interface so that the library can be used in Java projects.
 *  \par Example usage code in C++:
 *  \code
...

int status = 0;

WMBusConnection* connection = new WMBusConnection( "\\\\.\\COM3" );
status = connection->connect();
if ( status != WMBUS_ERR_NONE )
	return 1;

WMBusEncryption* encryption = new WMBusEncryption( AES_key );

WMBusMeter* meter = new WMBusMeter( secAddress, connection, encryption );
WMBUS_RESPONSE result;
status = meter->getObisValue( OBIS_code, &result );
if ( status != WMBUS_ERR_NONE )
	return 1;

...
 *  \endcode
 *  \par Example usage code in Java:
 *  Please note that the links in this Code refer to the C++ and not to the Java classes.
 *  \code
...

int status = 0;        
ErrorCode errorCode = new ErrorCode( ErrorCode.WMBUS_ERR_NONE );

WMBusConnection connection = new WMBusConnection( "\\\\.\\COM3" );
status = connection.connect();
if( status != ErrorCode.WMBUS_ERR_NONE )
    return;

WMBusEncryption encryption = new WMBusEncryption( AES_key );

WMBusMeter meter = new WMBusMeter( secAddress, connection, encryption );
WMBusResponse r = meter.getObisValue( obisCode, errorCode );

...
 *  \endcode
 *  
 *  @file libwmbus0.h
 *  General include directory to use this library in another project. It
 *  contains the general Classes that are intended to be externally used.
 *   
 *  @author Andreas Tetzner
 *  @date 26.10.2010
 * 
 *  @version 0.1
 *  Initial
 */

//#include "portability.h"
//#include "defines.h"
//#include <unistd.h>

#include "datatypes.h"
#include <termios.h>
#include <cstring>


#ifndef uint8_t
  typedef unsigned char uint8_t;
#endif

/// Number of columns in the state & expanded key in the AES encryption
#define Nb 4
/// Number of columns in a key in the AES encryption
#define Nk 4
/// Number of rounds in the AES encryption
#define Nr 10


////////////////////////////////////////////////////////////////////

/*! Implementation of the AES128 encryption that is used to secure the communication
 * between the meter and this library.
 */
class WMBusEncryption 
{
private:
	uint8_t AESkey[16];
	uint8_t AES_expkey[4 * Nb * (Nr + 1)];
	static const uint8_t Rcon[11];
	static const uint8_t XtimeE[256];
	static const uint8_t XtimeD[256];
	static const uint8_t XtimeB[256];
	static const uint8_t Xtime9[256];
	static const uint8_t Xtime3Sbox[256];
	static const uint8_t Xtime2Sbox[256];
	static const uint8_t InvSbox[256];
	static const uint8_t Sbox[256];

	void AES_ExpandKey (uint8_t *keyP);
	void AES_Encrypt (uint8_t *in, uint8_t *out);
	void AES_Decrypt (uint8_t *in, uint8_t *out);
	void AES_AddRoundKey(uint8_t *ARKstate, uint8_t *ARKkey, uint8_t *ARKout);

	void AES_ShiftRows (uint8_t *state, uint8_t *out);
	void AES_InvShiftRows (uint8_t *state, uint8_t *out);

	void AES_MixSubColumns(uint8_t *state, uint8_t *out, uint8_t *key);
	void AES_InvMixSubColumns(uint8_t *IMSCstate, uint8_t *IMSCout, uint8_t *IMSCkey);

public:
	/*! Constructor, initializing the class with a specific AES key. The constructor will
	 * consider the given key to be exactly 128bit big.
	 * \param AESkey Key that is used in the \c encrypt and \c decrypt methods
	 */
	WMBusEncryption( char* AESkey );

	/*! For a given \c data_frame, the \c data will be encoded with the key given to the
	 * constructor. The encryption will be performed in-place.
	 * \param frame Data frame with which the \c data will be sent
	 * \param data The data that is to be encrypted
	 * \param data_length The number of bytes that are to be encrypted
	 * \return Error code from the \c err_codes.h file.
	 */
	int encrypt( DATA_FRAME* frame, unsigned char* data, unsigned int data_length );
	
	/*! For a given \c data_frame, the \c data will be decrypted with the key given to the
	 * constructor. The decryption will be performed in-place.
	 * \param frame Data frame with which the \c data will be sent
	 * \param data The data that is to be decrypted
	 * \param data_length The number of bytes that are to be decrypted
	 * \return Error code from the \c err_codes.h file.
	 */
	int decrypt( DATA_FRAME* frame, unsigned char* data, unsigned int data_length );

	/*! Converts the given string with a hex number in ASCII chars into
	 * an array of \c byte s with the values from the string. The method will
	 * ignore the caracters ' ' and '-' but any other character except [a-fA-F0-9]
	 * will lead into an error. In the latter case, the method will return 
	 * \c NULL .
	 * \param string String with the ASCII hex number
	 * \param bytesAvailable Will be set to the number of bytes that are generated.
	 * \return \c byte array with the contents from \c string .
	 */
	static byte* hexStringToByte( const char* string, int* bytesAvailable );

	/*! Runs through the given string with hex numbers and returns the number of
	 * bytes that are encoded inside of the string. The method will ignore the 
	 * characters * * and *-* but any other character except [a-fA-F0-9] will
	 * lead into an error that is encoded by a negative return value.
	 * \param string String with the ASCII hex number
	 * \return Number of bytes that can be extracted from the string or -1 if there
	 *		are illegal characters in the given \c string .
	 */
	static int hexStringLength( const char* string );
};

