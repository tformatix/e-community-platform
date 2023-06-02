//------------------------------------------------------------------------------
// Author: Markus Flohberger
// E-Mail: Markus.Flohberger@energieag.at
// Changed: 2013-05-13 13:00h
// 
// Based on File by Robert Johansson, Raditex AB (rSCADA)
//
//------------------------------------------------------------------------------

#ifndef _AUXFUNCTIONS_H_
#define _AUXFUNCTIONS_H_

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>

#include <stdio.h>

extern "C" {
#include <mbus/mbus.h> 
}

int compare_mbus_search_header(mbus_data_search_frame_header *searched_header, mbus_data_search_frame_header *slave_search_header);
int compare_mbus_frames(mbus_frame* frame1, mbus_frame* frame2);
int set_mbus_frame(mbus_frame* frame1, mbus_frame* frame2);
double extract_amis_values(mbus_frame* frame, int index);
char* itoa(int value, char* result, int base); 
int hexStringLength( const char* string );
u_char* hexStringToByte( const char* string, int* bytesAvailable );

// Get current date/time, format is YYYY-MM-DD.HH:mm:ss
const char* currentDateTime();

void extract_date_time(struct tm *t, u_char *t_data, size_t t_data_size);

bool fileExists(const char* filename);
bool readKeyFromFile(const char* filename, char* key, int length);

#endif
