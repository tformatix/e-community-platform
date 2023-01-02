package at.fhooe.ecommunity.ui.screen.startup.login

import android.util.Log
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.AuthApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.LoginMemberModel
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.util.EncryptedPreferences
import kotlinx.coroutines.*

/**
 * view model for the "LoginScreen"
 * @param _application eCommunity application
 * @see LegacyLoadingStateViewModel
 */
class LoginViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {
    /**
     * initiates the cloud backend to login the user
     * @param _email email address
     * @param _password password
     */
    fun login(_email: String, _password: String) {
        Log.d(TAG, "login($_email)")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mState.emit(LoadingState(LoadingState.State.RUNNING, LoadingStateId.LOGIN.ordinal))

                val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)
                val login = authApi.authLoginPost(LoginMemberModel(_email, _password))

                // store tokens to the encrypted shared preferences
                EncryptedPreferences(mApplication).updateCredentials(login)

                mState.emit(LoadingState(LoadingState.State.SUCCESS, LoadingStateId.LOGIN.ordinal))
            } catch (_exc: Exception){
                // an error occurred
                mState.emit(LoadingState(LoadingState.State.FAILED, LoadingStateId.LOGIN.ordinal, _exc))
            }
        }
    }

    /**
     * initiates the cloud backend to resend a confirmation email to the user
     * @param _email email address
     * @param _password password
     */
    fun resendConfirmationEmail(_email: String, _password: String) {
        Log.d(TAG, "resendConfirmationEmail($_email)")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mState.emit(LoadingState(LoadingState.State.RUNNING, LoadingStateId.RESEND_CONFIRMATION_EMAIL.ordinal))

                val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)
                authApi.authResendConfirmationEmailPost(LoginMemberModel(_email, _password))

                mState.emit(LoadingState(LoadingState.State.SUCCESS, LoadingStateId.RESEND_CONFIRMATION_EMAIL.ordinal))
            } catch (_exc: Exception){
                // an error occurred
                mState.emit(LoadingState(LoadingState.State.FAILED, LoadingStateId.RESEND_CONFIRMATION_EMAIL.ordinal, _exc))
            }
        }
    }

    /**
     * possible state IDs of this view model
     */
    enum class LoadingStateId {
        LOGIN,
        RESEND_CONFIRMATION_EMAIL
    }
}