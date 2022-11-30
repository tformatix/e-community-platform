package at.fhooe.smartmeter.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences

class EncryptedPreferences(val context: Context) {
    var sharedPreferences: SharedPreferences? = null

    init {
        sharedPreferences = EncryptedSharedPreferences.create(
            Constants.SHARED_PREFS_FILE,
            Constants.SHARED_PREFS_KEY,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}