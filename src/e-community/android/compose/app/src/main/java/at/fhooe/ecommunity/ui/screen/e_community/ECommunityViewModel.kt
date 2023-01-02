package at.fhooe.ecommunity.ui.screen.e_community

import android.util.Log
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.DistributionApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.CurrentPortionDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.NewDistributionDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import java.util.*

class ECommunityViewModel(_application: ECommunityApplication) : LoadingStateViewModel(_application) {
    companion object {
        const val TAG_E_COMMUNITY_VM = "ECommunityViewModel"

        const val PERFORMANCE = 0
        const val CURRENT = 1
        const val NEW = 2
        const val MONITORING = 3
    }

    private val distributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)
    var currentPortion: CurrentPortionDto? = null
        private set
    var newDistribution: NewDistributionDto? = null
        private set

    fun performance(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::performance($_smartMeterId)")
    }

    fun currentPortion(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::currentPortion($_smartMeterId)")
        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(CURRENT)) {
            emitState(LoadingState(LoadingState.State.RUNNING, CURRENT))
            currentPortion = distributionApi.distributionCurrentPortionGet(_smartMeterId)
            emitState(LoadingState(LoadingState.State.SUCCESS, CURRENT))
        }
    }

    fun newDistribution() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::newDistribution()")
        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(NEW)) {
            emitState(LoadingState(LoadingState.State.RUNNING, NEW))
            newDistribution = distributionApi.distributionNewDistributionGet()
            emitState(LoadingState(LoadingState.State.SUCCESS, NEW))
        }
    }

    fun monitoring() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::monitoring()")
    }
}