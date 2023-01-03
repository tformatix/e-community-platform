package at.fhooe.ecommunity.ui.screen.e_community

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.DistributionApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.CurrentPortionDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.NewDistributionDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.NewPortionDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.PortionAckModel
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import java.util.*

class ECommunityViewModel private constructor(_application: ECommunityApplication) : LoadingStateViewModel(_application) {
    companion object {
        const val TAG_E_COMMUNITY_VM = "ECommunityViewModel"

        const val PERFORMANCE = 0
        const val CURRENT = 1
        const val NEW = 2
        const val MONITORING = 3
        const val PORTION_ACK = 4

        private var instance : ECommunityViewModel? = null

        fun getInstance(_application: ECommunityApplication): ECommunityViewModel {
            if (instance == null)  // NOT thread safe!
                instance = ECommunityViewModel(_application)
            return instance!!
        }
    }

    private val mDistributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)

    var mRunningOperations = mutableStateOf(0)
    val mCurrentPortion = mutableStateOf<CurrentPortionDto?>(null)
    val mNewDistribution = mutableStateOf<NewDistributionDto?>( null)

    fun init(){
        performance(UUID.fromString("6fb64e7f-b7f9-43e6-298e-08da59e57387")) // TODO
        currentPortion(UUID.fromString("6fb64e7f-b7f9-43e6-298e-08da59e57387")) // TODO
        newDistribution()
        monitoring()
    }

    fun performance(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::performance($_smartMeterId)")
        mRunningOperations.value++
        emitState(LoadingState(LoadingState.State.RUNNING, PERFORMANCE))
        emitState(LoadingState(LoadingState.State.SUCCESS, PERFORMANCE))
    }

    fun currentPortion(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::currentPortion($_smartMeterId)")
        mRunningOperations.value++
        mCurrentPortion.value = null
        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(CURRENT)) {
            emitState(LoadingState(LoadingState.State.RUNNING, CURRENT))
            mCurrentPortion.value = mDistributionApi.distributionCurrentPortionGet(_smartMeterId)
            emitState(LoadingState(LoadingState.State.SUCCESS, CURRENT))
        }
    }

    fun newDistribution() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::newDistribution()")
        mRunningOperations.value++
        mNewDistribution.value = null
        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(NEW)) {
            emitState(LoadingState(LoadingState.State.RUNNING, NEW))
            mNewDistribution.value = mDistributionApi.distributionNewDistributionGet()
            emitState(LoadingState(LoadingState.State.SUCCESS, NEW))
        }
    }

    fun monitoring() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::monitoring()")
        mRunningOperations.value++
        emitState(LoadingState(LoadingState.State.RUNNING, MONITORING))
        emitState(LoadingState(LoadingState.State.SUCCESS, MONITORING))
    }

    fun portionAck(_newPortion: NewPortionDto, _flexibility: Int) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::portionAck()")
        mRunningOperations.value++
        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))
            mDistributionApi.distributionHourlyPortionAckPost(PortionAckModel(_newPortion.smartMeterId, _flexibility))
            newDistribution()
            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }
}