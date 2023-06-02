package at.fhooe.ecommunity.ui.screen.startup.register

import android.util.Log
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.AuthApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.RegisterMemberModel
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * view model for "RegisterScreen"
 * @param _application eCommunity application
 * @see LegacyLoadingStateViewModel
 */
class RegisterViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {
    /**
     * initiates the cloud backend to create a new user account
     * @param _username name of the user
     * @param _email email address
     * @param _password password
     */
    fun register(_username: String, _email: String, _password: String) {
        Log.d(TAG, "register($_email)")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                val authApi = AuthApi(Constants.HTTP_BASE_URL_CLOUD)
                authApi.authRegisterPost(RegisterMemberModel(_email, _username, _password, Locale.getDefault().language))

                mState.emit(LoadingState(LoadingState.State.SUCCESS))
            } catch (_exc: Exception) {
                // an error occurred
                mState.emit(LoadingState(LoadingState.State.FAILED, mException = _exc))
            }
        }
    }
}