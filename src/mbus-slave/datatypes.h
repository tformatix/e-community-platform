
/** @file datatypes.h
 *  Common datatypes, that are used in this library.
 *   
 *  @author Andreas Tetzner
 *  @date 26.10.2010
 * 
 *  @version 0.1
 *  Initial
 */

#define byte unsigned char
#define MIN(a,b) ( (a) > (b) ? (b) : (a) )

////////////////////////////////////////////////////////////////////
/*! \def FRAME_HEADER_SIZE
 * Holds the size in bytes of the \c frame_header struct. This will be used
 * to read out responses from a meter directly to a struct instance.
 */
#define FRAME_HEADER_SIZE 14

/*! \struct frame_header 
 * This struct holds the header for a wM-Bus data package. The elements
 * of this struct are ordered, so that the binary data package can be
 * directly saved into an instance of this struct.
 */
struct frame_header {
	/// Overall length of the data package, excluding this field
	unsigned char length;
	/// C-Field
	unsigned char c_field;
	/// M-Field + A-Field
	unsigned char sec_addr[8];
	/// CI-Field
	unsigned char ci_field;
	/// Package number
	unsigned char acc_nr;
	/// Status
	unsigned char status;
	/// Packkage signature
	unsigned char signature[2];
};
/*! \typedef FRAME_HEADER 
 * \see frame_header
 */
typedef struct frame_header FRAME_HEADER;

/*! \struct data_frame
 * Extension to the standard \c frame_header struct; it additionally
 * includes a pointer to an array of raw data.
 * \see frame_header
 */
struct data_frame : frame_header {
	/*! Pointer to an array with the data that was received / is to be sent.
	 * The number of elements int he array must be calculated using 
	 * \c frame_header::length and \c FRAME_HEADER_SIZE .
	 */
	unsigned char* data;
	/// RSSI-Value
	unsigned char* rssi;
};
/*! \typedef DATA_FRAME 
 * \see data_frame
 */
typedef struct data_frame DATA_FRAME;

