//------------------------------------------------------------------------------
// Author: Markus Flohberger
// E-Mail: Markus.Flohberger@energieag.at
// Changed: 2013-05-13 13:00h
// 
// Based on File by Robert Johansson, Raditex AB (rSCADA)
//
//------------------------------------------------------------------------------

#include "auxfunctions.h"
#include <termios.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/select.h>
//#include <stropts.h>
#include "datatypes.h"

int compare_mbus_search_header(mbus_data_search_frame_header *searched_header, mbus_data_search_frame_header *slave_search_header)
{
	// mbus_data_search_frame_header tmp_header; UNUSED

	char slave_search_header_char[32];
	char searched_header_char[32];
	int length;

	length = snprintf(slave_search_header_char, sizeof(slave_search_header_char), "%02X%02X%02X%02X%02X%02X%02X%02X",
			slave_search_header->id_bcd[0],
			slave_search_header->id_bcd[1],
			slave_search_header->id_bcd[2],
			slave_search_header->id_bcd[3],
			slave_search_header->manufacturer[0],
			slave_search_header->manufacturer[1],
			slave_search_header->version,
			slave_search_header->medium);

	length = snprintf(searched_header_char, sizeof(searched_header_char), "%02X%02X%02X%02X%02X%02X%02X%02X",
			searched_header->id_bcd[0],
			searched_header->id_bcd[1],
			searched_header->id_bcd[2],
			searched_header->id_bcd[3],
			searched_header->manufacturer[0],
			searched_header->manufacturer[1],
			searched_header->version,
			searched_header->medium);

	int count = 0;
	int equal = 1;

	for(count=0; count<length; count++)
	{
		//printf("%d: %c %c [%d %d %d]\n", count, slave_search_header_char[count], searched_header_char[count], (slave_search_header_char[count] != searched_header_char[count]), ((searched_header_char[count] != 'f') && (searched_header_char[count] != 'F')), ((slave_search_header_char[count] != searched_header_char[count]) && ((searched_header_char[count] != 'f') && (searched_header_char[count] != 'F')) ));
		//printf("%d: f %d F %d \n", count, (searched_header_char[count] != 'f'), (searched_header_char[count] != 'F'));

		if((slave_search_header_char[count] != searched_header_char[count]) && ((searched_header_char[count] != 'f') && (searched_header_char[count] != 'F')) )
		{
			equal = 0;
			break;
		}
	}
	return equal;
}



int compare_mbus_frames(mbus_frame* frame1, mbus_frame* frame2)
{
	int equal = 1;
	int count = 0;

	if(frame1->type != frame2->type)
	{
		equal = 0;
		return equal;
	}

	switch (frame1->type)
	{
		case MBUS_FRAME_TYPE_ACK:
			break;

		case MBUS_FRAME_TYPE_SHORT:
			if(frame1->start1 != frame2->start1)
				equal=0;
			if(frame1->control != frame2->control)
				equal=0;
			if(frame1->address != frame2->address)
				equal=0;
			if(frame1->checksum != frame2->checksum)
				equal=0;
			if(frame1->stop != frame2->stop)
				equal=0;
			break;

		case MBUS_FRAME_TYPE_CONTROL:
			if(frame1->start1 != frame2->start1)
				equal=0;
			if(frame1->length1 != frame2->length1)
				equal=0;
			if(frame1->length2 != frame2->length2)
				equal=0;
			if(frame1->start2 != frame2->start2)
				equal=0;
			if(frame1->control != frame2->control)
				equal=0;
			if(frame1->control_information != frame2->control_information)
				equal=0;
			if(frame1->address != frame2->address)
				equal=0;
			if(frame1->checksum != frame2->checksum)
				equal=0;
			if(frame1->stop != frame2->stop)
				equal=0;
			break;

		case MBUS_FRAME_TYPE_LONG:
			if(frame1->start1 != frame2->start1)
				equal=0;
			if(frame1->length1 != frame2->length1)
				equal=0;
			if(frame1->length2 != frame2->length2)
				equal=0;
			if(frame1->start2 != frame2->start2)
				equal=0;
			if(frame1->control != frame2->control)
				equal=0;
			if(frame1->control_information != frame2->control_information)
				equal=0;
			if(frame1->address != frame2->address)
				equal=0;
			if(frame1->checksum != frame2->checksum)
				equal=0;
			if(frame1->stop != frame2->stop)
				equal=0;

			for(count=0; count<frame1->data_size; count++)
			{
				if (frame1->data[count] != frame2->data[count])
					equal = 0;
			} // end for loop: count

			break;
	}            

	return equal;
}


int set_mbus_frame(mbus_frame* frame1, mbus_frame* frame2)
{
	int count = 0;

	frame2->start1 = frame1->start1;
	frame2->length1 = frame1->length1;
	frame2->length2 = frame1->length2;
	frame2->start2 = frame1->start2;
	frame2->control = frame1->control;
	frame2->address = frame1->address;
	frame2->control_information = frame1->control_information;
	frame2->checksum = frame1->checksum;
	frame2->stop = frame1->stop;
	frame2->type = frame1->type;
	frame2->timestamp = frame1->timestamp;
	frame2->next = frame1->next;
	frame2->data_size = frame1->data_size;


	for(count=0; count<252; count++) // 252 maximum size defined in mbis-protocol.h
	{
		frame2->data[count] = frame1->data[count];
	} // end for loop: count


	return 0;
}


double extract_amis_values(mbus_frame* frame, int index)
{
	int val = (int)mbus_data_bcd_decode(frame->data+3, 4);  
	printf("val: %d\n", val);

	return 0;
}

/**
 * C++ version 0.4 char* style "itoa":
 * Written by Lukás Chmela
 * Released under GPLv3.
 */
char* itoa(int value, char* result, int base) {
	// check that the base if valid
	if (base < 2 || base > 36) { *result = '\0'; return result; }

	char* ptr = result, *ptr1 = result, tmp_char;
	int tmp_value;

	do {
		tmp_value = value;
		value /= base;
		*ptr++ = "zyxwvutsrqponmlkjihgfedcba9876543210123456789abcdefghijklmnopqrstuvwxyz" [35 + (tmp_value - value * base)];
	} while ( value );

	// Apply negative sign
	if (tmp_value < 0) *ptr++ = '-';
	*ptr-- = '\0';
	while(ptr1 < ptr) {
		tmp_char = *ptr;
		*ptr--= *ptr1;
		*ptr1++ = tmp_char;
	}
	return result;
}

u_char* hexStringToByte( const char* string, int* bytesAvailable )
{
	char* currChar = (char*) string;
	int byteLength = hexStringLength( string );

	// If an error occurs, return NULL - pointer
	if ( byteLength <= 0 )
		return NULL;

	// Parse the input string
	*bytesAvailable = byteLength;
	byte* result = new byte[ *bytesAvailable ];
	byte tempVal = 0;
	bool firstHalf = true;

	byte* currVal = result;
	currChar = (char*) string;
	while ( *currChar != '\0' )
	{
		tempVal = 0;

		// Ignore ' ' and '-'
		if ( *currChar == ' ' || *currChar == '-' )
		{
			currChar++;
			continue;
		}

		// Convert the hex-character to a number
		if ( *currChar >= '0' && *currChar <= '9' )
			tempVal = *currChar - '0';
		if ( *currChar >= 'a' && *currChar <= 'f' )
			tempVal = *currChar - 'a' + 10;
		if ( *currChar >= 'A' && *currChar <= 'F' )
			tempVal = *currChar - 'A' + 10;

		// Insert the value into the byte array
		if ( firstHalf )
		{
			*currVal = tempVal << 4;
			firstHalf = false;
		}
		else
		{
			*currVal += tempVal;
			firstHalf = true;
			currVal++;
		}

		currChar++;
	}

	return result;
}

int hexStringLength( const char* string )
{
	char* currChar = (char*) string;
	int relevantChars = 0;

	// Count the relevant characters ( 0-9, A-F )
	while ( *currChar != '\0' )
	{
		// Ignore ' ' and '-'
		if ( *currChar == ' ' || *currChar == '-' )
		{
			currChar++;
			continue;
		}

		// Check if the character is valid and if not, return an error
		if ( 
			( *currChar >= '0' && *currChar <= '9' ) ||
			( *currChar >= 'a' && *currChar <= 'f' ) ||
			( *currChar >= 'A' && *currChar <= 'F' )
			)
			relevantChars++;
		else
			return -1;

		currChar++;
	}

	return ( relevantChars / 2 ) + ( relevantChars % 2 );
}


// Get current date/time, format is YYYY-MM-DD HH:mm:ss
const char* currentDateTime() 
{
    time_t     now = time(0);
    struct tm  tstruct;
    static char       buf[100];
    tstruct = *localtime(&now);
    // Visit http://en.cppreference.com/w/cpp/chrono/c/strftime
    // for more information about date/time format
    strftime(buf, sizeof(buf), "%F %X", &tstruct);

    return buf;
}


//------------------------------------------------------------------------------
///
/// Decode time data (usable for type f = 4 bytes or type g = 2 bytes)
///
//------------------------------------------------------------------------------
void extract_date_time(struct tm *t, u_char *t_data, size_t t_data_size)
{
    if (t && t_data)
    {
        t->tm_sec   = 0;
        t->tm_min   = 0;
				t->tm_hour  = 0;
				t->tm_mday  = 0;
				t->tm_mon   = 0;
				t->tm_year  = 0; 
				t->tm_isdst = 0;

				if (t_data_size == 6)                // Type I = 48bit: Date and Time
				{
					if ((t_data[1] & 0x80) == 0)     // Time valid ?
					{
            t->tm_sec   = t_data[0] & 0x3F;
						t->tm_min   = t_data[1] & 0x3F;
						t->tm_hour  = t_data[2] & 0x1F;
						t->tm_mday  = t_data[3] & 0x1F;
						t->tm_mon   = (t_data[4] & 0x0F) - 1;
						t->tm_year  = ((t_data[3] & 0xE0) >> 5) | 
							((t_data[4] & 0xF0) >> 1); 
						t->tm_isdst = (t_data[0] & 0x40) ? 1 : 0;  // day saving time
					}
				}
				else if (t_data_size == 4)                // Type F = Compound CP32: Date and Time
				{     
					if ((t_data[0] & 0x80) == 0)     // Time valid ?
					{
						t->tm_min   = t_data[0] & 0x3F;
						t->tm_hour  = t_data[1] & 0x1F;
						t->tm_mday  = t_data[2] & 0x1F;
						t->tm_mon   = (t_data[3] & 0x0F) - 1;
						t->tm_year  = ((t_data[2] & 0xE0) >> 5) | 
							((t_data[3] & 0xF0) >> 1); 
						t->tm_isdst = (t_data[1] & 0x80) ? 1 : 0;  // day saving time
					}
				}
				else if (t_data_size == 2)           // Type G: Compound CP16: Date
				{
					t->tm_mday = t_data[0] & 0x1F;
					t->tm_mon  = (t_data[1] & 0x0F) - 1; 
					t->tm_year = ((t_data[0] & 0xE0) >> 5) | 
						((t_data[1] & 0xF0) >> 1);  
				}
		}
}

bool fileExists(const char* filename)
{
    FILE* fp = NULL;
 
    //this will fail if more capabilities to read the 
    //contents of the file is required (e.g. \private\...)
    fp = fopen(filename, "r");
 
    if(fp != NULL)
      {
        fclose(fp);
        return true;
      }
 
    return false;
}

bool readKeyFromFile(const char* filename, char* key, int length)
{
    FILE* fp = NULL;
 
    //this will fail if more capabilities to read the 
    //contents of the file is required (e.g. \private\...)
    fp = fopen(filename, "r");

		if(fp != NULL)
		{
			if ( fgets (key , length+1 , fp) != NULL )
			{
				return true;
			}
			else
			{
				return false;
			}
		}

		return false;
}

