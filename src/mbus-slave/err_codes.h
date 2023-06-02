
/** @file err_codes.h
 *  Contains the list of defines with the error codes that are
 *  used within the library.
 *   
 *  @author Andreas Tetzner
 *  @date 26.10.2010
 * 
 *  @version 0.1
 *  Initial
 */

/// No error has ocurred
#define WMBUS_ERR_NONE						0x00000
/// An unkonwn error has ocurred
#define WMBUS_ERR_UNKNOWN					0x00001
/// The given serial port is unkown and could not be opened
#define WMBUS_ERR_SERIAL_PORT_UNKOWN		0x00002
/// An error during the openeing of the serial port has ocurred
#define WMBUS_ERR_SERIAL_PORT_ERR			0x00003
/// Error during setting of the serial port properties
#define WMBUS_ERR_SERIAL_PORT_PROPERTIES	0x00004
/// Error during setting of the serial port timeouts
#define WMBUS_ERR_SERIAL_PORT_TIMEOUTS		0x00005
/// Serial port is already connected and can not be connected again
#define WMBUS_ERR_SERIAL_PORT_ALREADY_CONNECTED \
											0x00006
/// Serial port is not connected and therefore sending / receiving fails
#define WMBUS_ERR_SERIAL_PORT_NOT_CONNECTED	0x00007
/// Error when sending data over the serial port
#define WMBUS_ERR_SERIAL_PORT_WRITE_ERR		0x00008
/// Error when receiving data from the serial port
#define WMBUS_ERR_SERIAL_PORT_READ_ERR		0x00009
/*! The meter did not return a response in time; this may happen when a
 * faulty request was received by the meter.
 */
#define WMBUS_ERR_EMPTY_RESPONSE			0x0000a
/*! The decryption of a received package failed; this may happen when a
 * faulty package was received from the meter.
 */
#define WMBUS_ERR_DECRYPTION_ERROR			0x0000b
/*! The encryption of data failed; this may happen when the package for
 * encryption has a faulty structure.
 */
#define WMBUS_ERR_ENCRYPTION_ERROR			0x0000c
/// An error during the conversion of the received data ocurred.
#define WMBUS_ERR_CONVERSION_ERROR			0x0000e
/// There is not enough data for the conversion of the received data.
#define WMBUS_ERR_CONVERSION_NO_DATA		0x0000f
/// The given OBIS code could not be found in the mapping table.
#define WMBUS_ERR_OBIS_MAPPING_ERROR		0x00010
/// The user is not allowed to request this OBIS code.
#define WMBUS_ERR_OBIS_PERMISSION_DENIED	0x00011
/// The meter returned a VIF that is unknown to the internal mapping.
#define WMBUS_ERR_UNKNOWN_VIF				0x00012
/// The meter did not respond in time
#define WMBUS_ERR_NO_ANSWER					0x00013
#define WMBUS_ERR_WRONG_ANSWER				0x00014