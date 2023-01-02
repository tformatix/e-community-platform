package at.fhooe.ecommunity.ui.screen.startup.forgot_password

import android.util.Log
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.AuthApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ForgotPasswordModel
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * view model for the "ForgotPasswordScreen"
 * @param _application eCommunity application
 * @see LegacyLoadingStateViewModel
 */
class ForgotPasswordViewModel(_application: ECommunityApplication): LegacyLoadingStateViewModel(_application) {
    /**
     * initiates the cloud backend to send the "forgot password" email
     * @param _email email address
     */
    fun forgotPassword(_email: String) {
        Log.d(TAG, "forgotPassword($_email)")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)
                authApi.authForgotPasswordPost(ForgotPasswordModel(_email))

                mState.emit(LoadingState(LoadingState.State.SUCCESS))
            } catch (_exc: Exception){
                // an error occurred
                mState.emit(LoadingState(LoadingState.State.FAILED, mException = _exc))
            }
        }
    }
}