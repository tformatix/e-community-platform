package at.fhooe.ecommunity.util

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.LoginDto
import java.util.UUID

/**
 * store tokens in encrypted sharedPreferences (Android Key Store)
 * @param mContext context to access the sharedPreferences
 */
class EncryptedPreferences(private val mContext: Context) {

    var mSharedPreferences: SharedPreferences? = null

    // set params for master key
    private val mKeySpec = KeyGenParameterSpec.Builder(
        Constants.ENCRYPTED_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(Constants.ENCRYPTED_KEY_SIZE)
        .build()

    // build master key using params
    private val mMasterKey: MasterKey = MasterKey.Builder(mContext)
        .setKeyGenParameterSpec(mKeySpec)
        .build()

    // create encrypted sharedPreferences
    init {
        mSharedPreferences = EncryptedSharedPreferences.create(
            mContext,
            Constants.SHARED_PREFS_FILE,
            mMasterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Updates credentials in sharedPreferences
     */
    fun updateCredentials(_loginDto: LoginDto?) {
        if(_loginDto != null) {
            mSharedPreferences?.edit()
                ?.putString(mContext.getString(R.string.shared_prefs_access_token), _loginDto.accessToken)
                ?.putString(mContext.getString(R.string.shared_prefs_refresh_token), _loginDto.refreshToken)
                ?.putString(mContext.getString(R.string.shared_prefs_member_id), _loginDto.memberId.toString())
                ?.apply()
        } else {
            mSharedPreferences?.edit()?.clear()?.apply()
        }
    }

    fun getCredentials() : LoginDto? {
        val token = mSharedPreferences?.getString(mContext.getString(R.string.shared_prefs_access_token), null) ?: return null

        return LoginDto(
            memberId = UUID.fromString(mSharedPreferences?.getString(mContext.getString(R.string.shared_prefs_member_id), null)),
            accessToken = token,
            refreshToken = mSharedPreferences?.getString(mContext.getString(R.string.shared_prefs_refresh_token), null)
        )
    }
}