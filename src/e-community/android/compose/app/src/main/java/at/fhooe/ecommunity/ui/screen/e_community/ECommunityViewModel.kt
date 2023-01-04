package at.fhooe.ecommunity.ui.screen.e_community

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.DistributionApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.MonitoringApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.*
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
            if (instance == null)
                instance = ECommunityViewModel(_application)
            return instance!!
        }
    }

    private val mDistributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mMonitoringApi = MonitoringApi(Constants.HTTP_BASE_URL_CLOUD)

    var mRunningOperations = mutableStateOf(0)
    val mPerformance = mutableStateOf<PerformanceDto?>(null)
    val mCurrentPortion = mutableStateOf<CurrentPortionDto?>(null)
    val mNewDistribution = mutableStateOf<NewDistributionDto?>( null)
    val mMonitoringStatus = mutableStateListOf<MonitoringStatusDto>()

    fun init(){
        performance(UUID.fromString("6fb64e7f-b7f9-43e6-298e-08da59e57387"),1) // TODO
        currentPortion(UUID.fromString("6fb64e7f-b7f9-43e6-298e-08da59e57387")) // TODO
        newDistribution()
        monitoringStatus()
    }

    fun performance(_smartMeterId: UUID, _durationDays: Int) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::performance($_smartMeterId, $_durationDays)")

        mRunningOperations.value++
        mPerformance.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PERFORMANCE)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PERFORMANCE))

            mPerformance.value = mMonitoringApi.monitoringPerformanceGet(_smartMeterId, _durationDays)

            emitState(LoadingState(LoadingState.State.SUCCESS, PERFORMANCE))
        }
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

    fun monitoringStatus() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::monitoring()")
        mRunningOperations.value++
        mMonitoringStatus.clear()

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(MONITORING)) {
            emitState(LoadingState(LoadingState.State.RUNNING, MONITORING))

            mMonitoringStatus.addAll(mMonitoringApi.monitoringMonitoringStatusGet())

            emitState(LoadingState(LoadingState.State.SUCCESS, MONITORING))
        }
    }

    fun portionAck(_newPortion: NewPortionDto, _flexibility: Int) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::portionAck()")

        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))

            mDistributionApi.distributionHourlyPortionAckPost(PortionAckModel(_newPortion.smartMeterId, _flexibility))
            init()

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }

    fun muteCurrentHourMonitoring(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::muteCurrentHourMonitoring($_smartMeterId)")

        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))

            mMonitoringApi.monitoringMuteCurrentHourGet(_smartMeterId)
            init()

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }
}