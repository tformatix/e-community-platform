package at.fhooe.smartmeter.util

import android.content.Context
import at.fhooe.smartmeter.R
import local.org.openapitools.client.models.StatusDto
import org.openapitools.client.models.LoginDto
import java.security.AccessControlContext
import kotlin.math.log

object Util {

    public fun getConnectionString(statusDto: StatusDto): String {
        var conString = ""

        statusDto.isWiredConnected.let {
            if (it== true) {
                conString += "| Wired |"
            }
        }

        statusDto.wifiSSID.let {
            conString += "| Wifi: $it |"
        }

        return conString
    }

    /**
     * Updates credentials in sharedPreferences
     */
    fun updateCredentials(context: Context, loginDto: LoginDto) {
        val sharedPrefs = EncryptedPreferences(context)
        sharedPrefs.sharedPreferences?.edit()
            ?.putString(context.getString(R.string.shared_prefs_access_token), loginDto.accessToken)
            ?.putString(context.getString(R.string.shared_prefs_refresh_token), loginDto.refreshToken)
            ?.apply()
    }
}