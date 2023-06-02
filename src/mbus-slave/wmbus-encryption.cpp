#include "wmbus-encryption.h"
#include "err_codes.h"

WMBusEncryption::WMBusEncryption( char* key )
{
	memset( AESkey, 0, 4 * Nb * (Nr + 1) );

	int byteSize = 0;
	byte* hex_key = hexStringToByte( key, &byteSize );

	memcpy( AESkey, hex_key, MIN( byteSize, 16 ) );
	AES_ExpandKey( (uint8_t*) AESkey );
}

int WMBusEncryption::encrypt( DATA_FRAME* frame, unsigned char* data, unsigned int data_length )
{
	unsigned int i = 0;
	unsigned int j = 0;
	unsigned int blockNumber = 0;
	unsigned char xorVector[16];
	unsigned char ciphertext[16];

	if ( data[0] != 0x2F || data[1] != 0x2F || data_length % 16 != 0)
		return WMBUS_ERR_ENCRYPTION_ERROR;

	// Create the Init-Vector
	memcpy( xorVector, frame->sec_addr, sizeof( frame->sec_addr ) );

	for( i = 8; i < 16; i++ )
		xorVector[i] = frame->acc_nr;

	// Do the CBC
	blockNumber = data_length / 16;

	for( j = 0; j < blockNumber; j++ )
	{

		// XOR the data and the Init-Vector
		for( i = 0; i < 16; i++ )
			data[16*j + i] = data[16*j + i] ^ xorVector[i];

		// Do the encryption
		AES_Encrypt( (uint8_t*) (data + j*16), (uint8_t*) ciphertext );
		memcpy( data + j*16, ciphertext, 16 );

		// Prepare the next XOR vector
		memcpy( xorVector, ciphertext, 16 );
	}

	return WMBUS_ERR_NONE;
}

int WMBusEncryption::decrypt( DATA_FRAME* frame, unsigned char* data, unsigned int data_length )
{
	unsigned int i = 0;
	unsigned int j = 0;
	unsigned int blockNumber = 0;
	unsigned char xorVector[16];
	unsigned char plaintext[16];

	// Create the Init-Vector
	memcpy( xorVector, frame->sec_addr, sizeof( frame->sec_addr ) );

	for( i = 8; i < 16; i++ )
		xorVector[i] = frame->acc_nr;


	// Do the CBC
	blockNumber = data_length / 16;

	for( j = 0; j < blockNumber; j++ )
	{

		// Do the decryption
		AES_Decrypt( (uint8_t*) ( data + j*16 ), (uint8_t*) plaintext );

		// XOR the plaintext and the Init-Vector
		for( i = 0; i < 16; i++ )
			plaintext[i] = plaintext[i] ^ xorVector[i];

		memcpy( xorVector, data + j*16, 16 );
		memcpy( data + j*16, plaintext, 16 );

	}

	if ( data[0] != 0x2F || data[1] != 0x2F )
		return WMBUS_ERR_ENCRYPTION_ERROR;

	return WMBUS_ERR_NONE;
}

void WMBusEncryption::AES_ExpandKey (uint8_t *keyP)
{
	uint8_t tmp0, tmp1, tmp2, tmp3, tmp4;
	int idx,idx_tmp;

	for(idx = 0;
		idx < 16;
		idx++)
	{
		AES_expkey[idx] = *keyP++;
	}
	
	for( idx = Nk; idx < Nb * (Nr + 1); idx++ )
	{
		idx_tmp=idx * 4;
		tmp0 = AES_expkey[idx_tmp - 4];
		tmp1 = AES_expkey[idx_tmp - 3];
		tmp2 = AES_expkey[idx_tmp - 2];
		tmp3 = AES_expkey[idx_tmp - 1];
		if( (idx & 0x03) == 0x00)
		{
			tmp4 = tmp3;
			tmp3 = Sbox[tmp0];
			tmp0 = Sbox[tmp1] ^ Rcon[idx >> 2];
			tmp1 = Sbox[tmp2];
			tmp2 = Sbox[tmp4];
		}
		AES_expkey[idx_tmp] = AES_expkey[idx_tmp - 4*Nk] ^ tmp0;
		AES_expkey[idx_tmp + 1] = AES_expkey[idx_tmp - 4*Nk + 1] ^ tmp1;
		AES_expkey[idx_tmp + 2] = AES_expkey[idx_tmp - 4*Nk + 2] ^ tmp2;
		AES_expkey[idx_tmp + 3] = AES_expkey[idx_tmp - 4*Nk + 3] ^ tmp3;
	}
}

void WMBusEncryption::AES_Encrypt (uint8_t *in, uint8_t *out)
{
	
	uint8_t state[Nb * 4], tmp[Nb * 4];
	uint8_t *keyP;

	keyP = AES_expkey;

	AES_AddRoundKey (in, keyP, state);
	keyP += Nb * 4;

	AES_MixSubColumns (state, tmp, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (tmp, state, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (state, tmp, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (tmp, state, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (state, tmp, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (tmp, state, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (state, tmp, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (tmp, state, keyP);
	keyP += Nb * 4;

	AES_MixSubColumns (state, tmp, keyP);
	keyP += Nb * 4;

	AES_ShiftRows (tmp, state);
	AES_AddRoundKey (state, keyP, out);
}

void WMBusEncryption::AES_Decrypt (uint8_t *in, uint8_t *out)
{
	
	uint8_t state[Nb * 4], tmp[Nb * 4];
	uint8_t *keyP;

	keyP = AES_expkey;

	keyP += Nr * Nb * 4;
	AES_AddRoundKey (in, keyP, tmp);
	AES_InvShiftRows(tmp, state);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (state, tmp, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (tmp, state, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (state, tmp, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (tmp, state, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (state, tmp, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (tmp, state, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (state, tmp, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (tmp, state, keyP);

	keyP -= Nb * 4;
	AES_InvMixSubColumns (state, tmp, keyP);

	keyP -= Nb * 4;
	AES_AddRoundKey (tmp, keyP, out);
}

void WMBusEncryption::AES_AddRoundKey(uint8_t *ARKstate, uint8_t *ARKkey, uint8_t *ARKout)
{
	*(ARKout) = *(ARKstate) ^ *(ARKkey);
	*(ARKout+1) = *(ARKstate+1) ^ *(ARKkey+1);
	*(ARKout+2) = *(ARKstate+2) ^ *(ARKkey+2);
	*(ARKout+3) = *(ARKstate+3) ^ *(ARKkey+3);
	*(ARKout+4) = *(ARKstate+4) ^ *(ARKkey+4);
	*(ARKout+5) = *(ARKstate+5) ^ *(ARKkey+5);
	*(ARKout+6) = *(ARKstate+6) ^ *(ARKkey+6);
	*(ARKout+7) = *(ARKstate+7) ^ *(ARKkey+7);
	*(ARKout+8) = *(ARKstate+8) ^ *(ARKkey+8);
	*(ARKout+9) = *(ARKstate+9) ^ *(ARKkey+9);
	*(ARKout+10) = *(ARKstate+10) ^ *(ARKkey+10);
	*(ARKout+11) = *(ARKstate+11) ^ *(ARKkey+11);
	*(ARKout+12) = *(ARKstate+12) ^ *(ARKkey+12);
	*(ARKout+13) = *(ARKstate+13) ^ *(ARKkey+13);
	*(ARKout+14) = *(ARKstate+14) ^ *(ARKkey+14);
	*(ARKout+15) = *(ARKstate+15) ^ *(ARKkey+15);
}

void WMBusEncryption::AES_MixSubColumns(uint8_t *state, uint8_t *out, uint8_t *key)
{
	/* mixing column 0*/
	out[0] = Xtime2Sbox[state[0]] ^ Xtime3Sbox[state[5]] ^ Sbox[state[10]] ^ Sbox[state[15]] ^ key[0];
	out[1] = Sbox[state[0]] ^ Xtime2Sbox[state[5]] ^ Xtime3Sbox[state[10]] ^ Sbox[state[15]] ^ key[1];
	out[2] = Sbox[state[0]] ^ Sbox[state[5]] ^ Xtime2Sbox[state[10]] ^ Xtime3Sbox[state[15]] ^ key[2];
	out[3] = Xtime3Sbox[state[0]] ^ Sbox[state[5]] ^ Sbox[state[10]] ^ Xtime2Sbox[state[15]] ^ key[3];

	/* mixing column 1*/
	out[4] = Xtime2Sbox[state[4]] ^ Xtime3Sbox[state[9]] ^ Sbox[state[14]] ^ Sbox[state[3]] ^ key[4];
	out[5] = Sbox[state[4]] ^ Xtime2Sbox[state[9]] ^ Xtime3Sbox[state[14]] ^ Sbox[state[3]] ^ key[5];
	out[6] = Sbox[state[4]] ^ Sbox[state[9]] ^ Xtime2Sbox[state[14]] ^ Xtime3Sbox[state[3]] ^ key[6];
	out[7] = Xtime3Sbox[state[4]] ^ Sbox[state[9]] ^ Sbox[state[14]] ^ Xtime2Sbox[state[3]] ^ key[7];

	/* mixing column 2*/
	out[8] = Xtime2Sbox[state[8]] ^ Xtime3Sbox[state[13]] ^ Sbox[state[2]] ^ Sbox[state[7]] ^ key[8];
	out[9] = Sbox[state[8]] ^ Xtime2Sbox[state[13]] ^ Xtime3Sbox[state[2]] ^ Sbox[state[7]] ^ key[9];
	out[10]  = Sbox[state[8]] ^ Sbox[state[13]] ^ Xtime2Sbox[state[2]] ^ Xtime3Sbox[state[7]] ^ key[10];
	out[11]  = Xtime3Sbox[state[8]] ^ Sbox[state[13]] ^ Sbox[state[2]] ^ Xtime2Sbox[state[7]] ^ key[11];

	/* mixing column 3*/
	out[12] = Xtime2Sbox[state[12]] ^ Xtime3Sbox[state[1]] ^ Sbox[state[6]] ^ Sbox[state[11]] ^ key[12];
	out[13] = Sbox[state[12]] ^ Xtime2Sbox[state[1]] ^ Xtime3Sbox[state[6]] ^ Sbox[state[11]] ^ key[13];
	out[14] = Sbox[state[12]] ^ Sbox[state[1]] ^ Xtime2Sbox[state[6]] ^ Xtime3Sbox[state[11]] ^ key[14];
	out[15] = Xtime3Sbox[state[12]] ^ Sbox[state[1]] ^ Sbox[state[6]] ^ Xtime2Sbox[state[11]] ^ key[15];

}


void WMBusEncryption::AES_InvMixSubColumns(uint8_t *IMSCstate, uint8_t *IMSCout, uint8_t *IMSCkey)
{
	uint8_t t0, t1, t2, t3;

	/* restore column 0*/
	t0 = *(IMSCstate + 0) ^ *(IMSCkey + 0);
	t1 = *(IMSCstate + 1) ^ *(IMSCkey + 1);
	t2 = *(IMSCstate + 2) ^ *(IMSCkey + 2);
	t3 = *(IMSCstate + 3) ^ *(IMSCkey + 3);
	*(IMSCout + 0) = InvSbox[XtimeE[t0] ^ XtimeB[t1] ^ XtimeD[t2] ^ Xtime9[t3]];
	*(IMSCout + 5) = InvSbox[Xtime9[t0] ^ XtimeE[t1] ^ XtimeB[t2] ^ XtimeD[t3]];
	*(IMSCout + 10) = InvSbox[XtimeD[t0] ^ Xtime9[t1] ^ XtimeE[t2] ^ XtimeB[t3]];
	*(IMSCout + 15) = InvSbox[XtimeB[t0] ^ XtimeD[t1] ^ Xtime9[t2] ^ XtimeE[t3]];

	/* restore column 1*/
	t0 = *(IMSCstate + 4) ^ *(IMSCkey + 4);
	t1 = *(IMSCstate + 5) ^ *(IMSCkey + 5);
	t2 = *(IMSCstate + 6) ^ *(IMSCkey + 6);
	t3 = *(IMSCstate + 7) ^ *(IMSCkey + 7);
	*(IMSCout + 4) = InvSbox[XtimeE[t0] ^ XtimeB[t1] ^ XtimeD[t2] ^ Xtime9[t3]];
	*(IMSCout + 9) = InvSbox[Xtime9[t0] ^ XtimeE[t1] ^ XtimeB[t2] ^ XtimeD[t3]];
	*(IMSCout + 14) = InvSbox[XtimeD[t0] ^ Xtime9[t1] ^ XtimeE[t2] ^ XtimeB[t3]];
	*(IMSCout + 3) = InvSbox[XtimeB[t0] ^ XtimeD[t1] ^ Xtime9[t2] ^ XtimeE[t3]];

	/* restore column 2*/
	t0 = *(IMSCstate + 8) ^ *(IMSCkey + 8);
	t1 = *(IMSCstate + 9) ^ *(IMSCkey + 9);
	t2 = *(IMSCstate + 10) ^ *(IMSCkey + 10);
	t3 = *(IMSCstate + 11) ^ *(IMSCkey + 11);
	*(IMSCout + 8) = InvSbox[XtimeE[t0] ^ XtimeB[t1] ^ XtimeD[t2] ^ Xtime9[t3]];
	*(IMSCout + 13) = InvSbox[Xtime9[t0] ^ XtimeE[t1] ^ XtimeB[t2] ^ XtimeD[t3]];
	*(IMSCout + 2)  = InvSbox[XtimeD[t0] ^ Xtime9[t1] ^ XtimeE[t2] ^ XtimeB[t3]];
	*(IMSCout + 7)  = InvSbox[XtimeB[t0] ^ XtimeD[t1] ^ Xtime9[t2] ^ XtimeE[t3]];

	/* restore column 3*/
	t0 = *(IMSCstate + 12) ^ *(IMSCkey + 12);
	t1 = *(IMSCstate + 13) ^ *(IMSCkey + 13);
	t2 = *(IMSCstate + 14) ^ *(IMSCkey + 14);
	t3 = *(IMSCstate + 15) ^ *(IMSCkey + 15);
	*(IMSCout + 12) = InvSbox[XtimeE[t0] ^ XtimeB[t1] ^ XtimeD[t2] ^ Xtime9[t3]];
	*(IMSCout + 1) = InvSbox[Xtime9[t0] ^ XtimeE[t1] ^ XtimeB[t2] ^ XtimeD[t3]];
	*(IMSCout + 6) = InvSbox[XtimeD[t0] ^ Xtime9[t1] ^ XtimeE[t2] ^ XtimeB[t3]];
	*(IMSCout + 11) = InvSbox[XtimeB[t0] ^ XtimeD[t1] ^ Xtime9[t2] ^ XtimeE[t3]];
}

void WMBusEncryption::AES_ShiftRows (uint8_t *state, uint8_t *out)
{
	// just substitute row 0
	out[0] = Sbox[state[0]], out[4] = Sbox[state[4]];
	out[8] = Sbox[state[8]], out[12] = Sbox[state[12]];

	// rotate row 1
	out[1] = Sbox[state[5]], out[5] = Sbox[state[9]];
	out[9] = Sbox[state[13]], out[13] = Sbox[state[1]];

	// rotate row 2
	out[2] = Sbox[state[10]], out[10] = Sbox[state[2]];
	out[6] = Sbox[state[14]], out[14] = Sbox[state[6]];

	// rotate row 3
	out[15] = Sbox[state[11]], out[11] = Sbox[state[7]];
	out[7] = Sbox[state[3]], out[3] = Sbox[state[15]];
}

void WMBusEncryption::AES_InvShiftRows (uint8_t *state, uint8_t *out)
{
	// restore row 0
	out[0] = InvSbox[state[0]], out[4] = InvSbox[state[4]];
	out[8] = InvSbox[state[8]], out[12] = InvSbox[state[12]];

	// restore row 1
	out[13] = InvSbox[state[9]], out[9] = InvSbox[state[5]];
	out[5] = InvSbox[state[1]], out[1] = InvSbox[state[13]];

	// restore row 2
	out[2] = InvSbox[state[10]], out[10] = InvSbox[state[2]];
	out[6] = InvSbox[state[14]], out[14] = InvSbox[state[6]];

	// restore row 3
	out[3] = InvSbox[state[7]], out[7] = InvSbox[state[11]];
	out[11] = InvSbox[state[15]], out[15] = InvSbox[state[3]];
}


byte* WMBusEncryption::hexStringToByte( const char* string, int* bytesAvailable )
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

int WMBusEncryption::hexStringLength( const char* string )
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
