package at.fhooe.smartmeter.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.infrastructure.ApiClient

const val TAG_AUTH = "Auth"

/**
 * helper for auth
 */
object Api {
    private var sharedPrefsGeneral: SharedPreferences? = null

    /**
     * authorized backend call (refreshes token in http header)
     * -> token problems --> go back to AuthActivity
     * -> initial opening of app --> read provider id and write to shared prefs
     * @param activity current activity
     * @param progressBar change visibility of progress bar
     * @param backendCall lambda call which gets token and returns nothing
     */
    fun authorizedBackendCall(context: Context, progressBar: ProgressBar?, backendCall: (token: String) -> Unit) {
        // api call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)

                val sharedPrefs = EncryptedPreferences(context)
                val refreshToken = sharedPrefs.sharedPreferences?.getString(context.getString(R.string.shared_prefs_refresh_token), "not authorized")
                val loginDto = authApi.authRefreshPost(refreshToken)

                Util.updateCredentials(context, loginDto)

                // insert token into http header
                ApiClient.apiKey[Constants.HTTP_AUTHORIZATION_KEY] = loginDto.accessToken.toString()
                ApiClient.apiKeyPrefix[Constants.HTTP_AUTHORIZATION_KEY] = Constants.HTTP_AUTHORIZATION_PREFIX

                loginDto.accessToken?.let {
                    backendCall(it)
                    return@launch
                }

                backendCall("")
            } catch (exc: java.lang.Exception) {
                // failure
                Log.d(TAG, "$TAG_AUTH::ERROR::${exc.message}")
            }
        }

        /*Api.authorizedBackendCall(activity, binding.fragmentDashboardProgress) {
            if(!isRefreshTimerSet)
                refreshActiveOrders()

            // read rejections (states)
            Log.d(Constants.TAG, "$TAG_DASHBOARD::GET Rejections")
            rejections = rejectionsApi.rejectionsGet()

            // all duration states
            Log.d(Constants.TAG, "$TAG_DASHBOARD::GET Duration States")
            durationStates = providersApi.providersIdDurationStatesGet(providerId)

            // provider
            Log.d(Constants.TAG, "$TAG_DASHBOARD::GET Provider")
            provider = providersApi.providersIdGet(providerId)
            activity?.runOnUiThread {
                durationStateIconUpdate(durationStates.indexOfFirst {
                    it.id == provider.selectedDurationState?.id
                })
                overloadedIconUpdate()
            }
        }*/
    }
}