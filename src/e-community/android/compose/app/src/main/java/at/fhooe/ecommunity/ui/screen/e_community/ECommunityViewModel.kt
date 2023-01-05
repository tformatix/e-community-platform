package at.fhooe.ecommunity.ui.screen.e_community

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.DistributionApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.ECommunityApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.MonitoringApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SmartMeterApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.*
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import java.util.*

class ECommunityViewModel private constructor(_application: ECommunityApplication) : LoadingStateViewModel(_application) {
    companion object {
        const val TAG_E_COMMUNITY_VM = "ECommunityViewModel"

        const val BASE_DATA = 0
        const val PERFORMANCE = 1
        const val CURRENT = 2
        const val NEW = 3
        const val MONITORING = 4
        const val PORTION_ACK = 5

        private var instance: ECommunityViewModel? = null

        fun getInstance(_application: ECommunityApplication): ECommunityViewModel {
            if (instance == null)
                instance = ECommunityViewModel(_application)
            return instance!!
        }
    }

    private val mECommunityApi = ECommunityApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mSmartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mDistributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mMonitoringApi = MonitoringApi(Constants.HTTP_BASE_URL_CLOUD)

    var mRunningOperations = mutableStateOf(0)

    val mSmartMeters = mutableStateListOf<MinimalSmartMeterDto>()
    val mECommunity = mutableStateOf<MinimalECommunityDto?>(null)

    val mPerformance = mutableStateOf<PerformanceDto?>(null)
    val mCurrentPortion = mutableStateOf<CurrentPortionDto?>(null)

    val mNewDistribution = mutableStateOf<NewDistributionDto?>(null)
    val mMonitoringStatus = mutableStateListOf<MonitoringStatusDto>()

    fun init(_includeBase: Boolean = true) {
        if(_includeBase) loadBaseData()
        loadPerformance(UUID.fromString("6fb64e7f-b7f9-43e6-298e-08da59e57387"), Constants.DEFAULT_PERFORMANCE_DURATION_DAYS) // TODO
        loadCurrentPortion(UUID.fromString("6fb64e7f-b7f9-43e6-298e-08da59e57387")) // TODO
        loadNewDistribution()
        loadMonitoringStatus()
    }

    fun loadBaseData () {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadBaseData()")

        mRunningOperations.value++
        mSmartMeters.clear()
        mECommunity.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(BASE_DATA)) {
            emitState(LoadingState(LoadingState.State.RUNNING, BASE_DATA))

            mECommunity.value = mECommunityApi.eCommunityGetMinimalECommunityGet()
            mSmartMeters.addAll(mSmartMeterApi.smartMeterGetMinimalSmartMetersGet())

            emitState(LoadingState(LoadingState.State.SUCCESS, BASE_DATA))
        }
    }

    fun loadPerformance(_smartMeterId: UUID, _durationDays: Int) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadPerformance($_smartMeterId, $_durationDays)")

        mRunningOperations.value++
        mPerformance.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PERFORMANCE)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PERFORMANCE))

            mPerformance.value = mMonitoringApi.monitoringPerformanceGet(_smartMeterId, _durationDays)

            emitState(LoadingState(LoadingState.State.SUCCESS, PERFORMANCE))
        }
    }

    fun loadCurrentPortion(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadCurrentPortion($_smartMeterId)")

        mRunningOperations.value++
        mCurrentPortion.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(CURRENT)) {
            emitState(LoadingState(LoadingState.State.RUNNING, CURRENT))

            mCurrentPortion.value = mDistributionApi.distributionCurrentPortionGet(_smartMeterId)

            emitState(LoadingState(LoadingState.State.SUCCESS, CURRENT))
        }
    }

    fun loadNewDistribution() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadNewDistribution()")

        mRunningOperations.value++
        mNewDistribution.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(NEW)) {
            emitState(LoadingState(LoadingState.State.RUNNING, NEW))

            mNewDistribution.value = mDistributionApi.distributionNewDistributionGet()

            emitState(LoadingState(LoadingState.State.SUCCESS, NEW))
        }
    }

    fun loadMonitoringStatus() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadMonitoringStatus()")
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

    fun toggleMute(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::muteCurrentHourMonitoring($_smartMeterId)")

        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))

            mMonitoringApi.monitoringToggleMuteCurrentHourGet(_smartMeterId)
            init()

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }
}