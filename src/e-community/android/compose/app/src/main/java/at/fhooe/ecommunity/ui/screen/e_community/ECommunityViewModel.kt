package at.fhooe.ecommunity.ui.screen.e_community

import android.util.Log
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.AuthApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.DistributionApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.LoginMemberModel
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import at.fhooe.ecommunity.ui.screen.startup.login.LoginViewModel
import at.fhooe.ecommunity.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ECommunityViewModel(_application: ECommunityApplication) : LoadingStateViewModel(_application) {

    companion object {
        const val TAG_E_COMMUNITY_VM = "ECommunityViewModel"

        const val PERFORMANCE = 0
        const val CURRENT = 1
        const val NEW = 2
        const val MONITORING = 3
    }

    private val distributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)

    fun performance(_smartMeterId: UUID) {
    }

    fun currentPortion(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::currentPortion($_smartMeterId)")
        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(CURRENT)) {
            emitState(LoadingState(LoadingState.State.RUNNING, CURRENT))

            distributionApi.distributionCurrentPortionGet(_smartMeterId)

            emitState(LoadingState(LoadingState.State.SUCCESS, CURRENT))
        }
    }
}