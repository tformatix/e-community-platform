package at.fhooe.ecommunity.data.remote.repository

import android.content.Intent
import android.util.Log
import android.widget.Toast
import at.fhooe.ecommunity.*
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.AuthApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ApiClient
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.LoginDto
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Cloud REST functionality
 * @param mApplication eCommunity application
 */
class CloudRESTRepository(private val mApplication: ECommunityApplication) {
    /**
     * authorized backend call (renews refresh token and puts access token into HTTP header)
     * if refresh token is invalid -> go back to StartUpActivity
     * @param _exceptionHandler handler for exceptions
     * @param _backendCall lambda call which gets token and returns nothing
     */
    fun authorizedBackendCall(_exceptionHandler: CoroutineExceptionHandler? = null, _backendCall: (token: String) -> Unit) {
        // add exception handler (if available) to coroutine context
        val coroutineContext =
            if (_exceptionHandler == null) Dispatchers.IO
            else Dispatchers.IO + _exceptionHandler

        CoroutineScope(coroutineContext).launch {
            try {
                val login = authorize()
                if (login == null) {
                    goToStartUpActivity()
                    return@launch
                } else {
                    // authorized
                    login.accessToken?.let { token ->
                        _backendCall(token)
                        return@launch
                    }
                }
            } catch (_exc: Exception) {
                //Toast.makeText(mApplication, mApplication.remoteExceptionRepository.exceptionToString(_exc), Toast.LENGTH_LONG)
                //    .show() // show error message
                throw _exc
            }
        }
    }

    /**
     * renews refresh token and puts access token into HTTP header
     * @return a LoginDto containing the tokens (null if not successful)
     */
    fun authorize(): LoginDto? {
        val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)

        val sharedPrefs = EncryptedPreferences(mApplication)
        val refreshToken = sharedPrefs.mSharedPreferences?.getString(mApplication.getString(R.string.shared_prefs_refresh_token), null) ?: return null

        try {
            val loginDto = authApi.authRefreshPost(refreshToken) // try refreshing the tokens

            sharedPrefs.updateCredentials(loginDto) // update tokens
            loginDto.accessToken?.let { token ->
                // insert token into http header
                ApiClient.apiKey[Constants.HTTP_AUTHORIZATION_KEY] = token
                ApiClient.apiKeyPrefix[Constants.HTTP_AUTHORIZATION_KEY] = Constants.HTTP_AUTHORIZATION_PREFIX
            }
            return loginDto
        } catch (_exc: Exception) {
            // an error occurred while refreshing the tokens
            val remoteException = mApplication.remoteExceptionRepository.exceptionToRemoteException(_exc)
            if (remoteException.mCategory == RemoteException.Category.CLIENT_ERROR) {
                // if the error is an Client Error (f.e. INVALID_REFRESH_TOKEN)
                sharedPrefs.updateCredentials(null) // set refresh and access token to null
                return null
            }
            throw _exc
        }
    }

    /**
     * go to StartUpActivity
     */
    private fun goToStartUpActivity() {
        Log.e(TAG, "goToStartUpActivity()")
        val intent = Intent(mApplication.applicationContext, StartUpActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mApplication.applicationContext.startActivity(intent)
    }
}