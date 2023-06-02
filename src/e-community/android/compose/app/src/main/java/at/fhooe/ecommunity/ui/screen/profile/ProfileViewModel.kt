package at.fhooe.ecommunity.ui.screen.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.MemberApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SmartMeterApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalMemberDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalSmartMeterDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * viewModel for Home Screen
 * @param _application application object
 */
class ProfileViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {
    // list of smart meters
    val mSmartMeters = MutableStateFlow(List(init = { MinimalSmartMeterDto() }, size = 0))

    fun loadProfile(_member: MutableState<MinimalMemberDto>) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        cloudRESTRepository.authorizedBackendCall(null) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                val memberApi = MemberApi(Constants.HTTP_BASE_URL_CLOUD)
                val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                try {
                    val member = memberApi.memberGetMinimalMemberGet()
                    val smartMeters = smartMeterApi.smartMeterGetMinimalSmartMetersGet()

                    _member.value = member
                    mSmartMeters.value = smartMeters

                    mState.emit(LoadingState(LoadingState.State.SUCCESS))
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    /**
     * logout user, delete refresh token
     */
    fun logout() {
        EncryptedPreferences(mApplication).updateCredentials(null)
    }
}