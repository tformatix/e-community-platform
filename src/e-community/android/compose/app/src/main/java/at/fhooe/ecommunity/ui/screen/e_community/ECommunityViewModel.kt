package at.fhooe.ecommunity.ui.screen.e_community

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.DistributionApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.ECommunityApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.MonitoringApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SmartMeterApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.*
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import at.fhooe.ecommunity.data.remote.signalr.dto.MeterDataRTDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import at.fhooe.ecommunity.ui.screen.home.HomeViewModel
import com.google.gson.Gson
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
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
        const val REQUEST_RT_DATA = 6
        const val STOP_RT_DATA = 7
        const val EXTEND_RT_DATA = 8

        private var instance: ECommunityViewModel? = null

        fun getInstance(_application: ECommunityApplication): ECommunityViewModel {
            if (instance == null)
                instance = ECommunityViewModel(_application)
            return instance!!
        }
    }

    private var mCloudSignalRRepository = CloudSignalRRepository()
    private val mECommunityApi = ECommunityApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mSmartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mDistributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mMonitoringApi = MonitoringApi(Constants.HTTP_BASE_URL_CLOUD)

    var mRunningOperations = mutableStateOf(0)

    val mSmartMeters = mutableStateListOf<MinimalSmartMeterDto>()
    val mSelectedSmartMeterIdx = mutableStateOf(0)
    val mECommunity = mutableStateOf<MinimalECommunityDto?>(null)

    val mMeterDataRT = mutableStateOf<BufferedMeterDataRTDto?>(null)
    private var mExtendTimer = Timer()
    private var mTimerCount = 0

    val mPerformance = mutableStateOf<PerformanceDto?>(null)
    val mCurrentPortion = mutableStateOf<CurrentPortionDto?>(null)

    val mNewDistribution = mutableStateOf<NewDistributionDto?>(null)
    val mMonitoringStatus = mutableStateListOf<MonitoringStatusDto>()

    fun initLoad() {
        mRunningOperations.value = 0
        loadBaseData()
        loadNewDistribution()
        loadMonitoringStatus()
    }

    private fun loadBaseData() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadBaseData()")

        mRunningOperations.value++
        mSmartMeters.clear()
        mECommunity.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(BASE_DATA)) {
            emitState(LoadingState(LoadingState.State.RUNNING, BASE_DATA))

            mECommunity.value = mECommunityApi.eCommunityGetMinimalECommunityGet()
            mSmartMeters.addAll(mSmartMeterApi.smartMeterGetMinimalSmartMetersGet())

            loadSmartMeterDependent()

            emitState(LoadingState(LoadingState.State.SUCCESS, BASE_DATA))
        }
    }

    fun loadSmartMeterDependent() {
        loadPerformance(Constants.DEFAULT_PERFORMANCE_DURATION_DAYS)
        loadCurrentPortion()
    }

    fun loadPerformance(_durationDays: Int) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadPerformance($_durationDays)")

        val smartMeterId = mSmartMeters.getOrNull(mSelectedSmartMeterIdx.value)?.id
        if (smartMeterId != null) {
            mRunningOperations.value++
            mPerformance.value = null

            mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PERFORMANCE)) {
                emitState(LoadingState(LoadingState.State.RUNNING, PERFORMANCE))

                mPerformance.value = mMonitoringApi.monitoringPerformanceGet(smartMeterId, _durationDays)

                emitState(LoadingState(LoadingState.State.SUCCESS, PERFORMANCE))
            }
        }
    }

    private fun loadCurrentPortion() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadCurrentPortion()")

        val smartMeterId = mSmartMeters.getOrNull(mSelectedSmartMeterIdx.value)?.id
        if (smartMeterId != null) {
            mRunningOperations.value++
            mCurrentPortion.value = null

            mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(CURRENT)) {
                emitState(LoadingState(LoadingState.State.RUNNING, CURRENT))

                mCurrentPortion.value = mDistributionApi.distributionCurrentPortionGet(smartMeterId)

                emitState(LoadingState(LoadingState.State.SUCCESS, CURRENT))
            }
        }
    }


    private fun loadNewDistribution() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadNewDistribution()")

        mRunningOperations.value++
        mNewDistribution.value = null

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(NEW)) {
            emitState(LoadingState(LoadingState.State.RUNNING, NEW))

            mNewDistribution.value = mDistributionApi.distributionNewDistributionGet()

            emitState(LoadingState(LoadingState.State.SUCCESS, NEW))
        }
    }

    private fun loadMonitoringStatus() {
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
            initLoad()

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }

    fun toggleMute(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::muteCurrentHourMonitoring($_smartMeterId)")

        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))

            mMonitoringApi.monitoringToggleMuteCurrentHourGet(_smartMeterId)
            initLoad()

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }

    //region SignalR
    /**
     * start SignalR listener
     */
    private fun startSignalR(_accessToken: String) {
        val connStr = Constants.HTTP_BASE_URL_CLOUD + Constants.SIGNALR_URL
        mCloudSignalRRepository = CloudSignalRRepository()

        mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD) { data ->
            run {
                val json = JSONObject(data.toMap()) // json object of server data
                mMeterDataRT.value = Gson().fromJson(json.toString(), BufferedMeterDataRTDto::class.java)
            }
        }

        // start connection
        mCloudSignalRRepository.let {
            val started = it.startConnection()

            if (started) {
                Log.d(TAG, "$TAG_E_COMMUNITY_VM::startSignalR() SignalR try starting")
            } else {
                Log.e(TAG, "$TAG_E_COMMUNITY_VM::startSignalR() SignalR not started")
            }
        }
    }

    /**
     * request RT data from cloud
     */
    fun requestRTDataStart(_requestStart: Boolean = true) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::requestRTDataStart()")
        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(REQUEST_RT_DATA)) { token ->
            emitState(LoadingState(LoadingState.State.RUNNING, REQUEST_RT_DATA))

            startSignalR(token)
            if (_requestStart) {
                val response = mSmartMeterApi.smartMeterRequestRTDataGet()
                val state = response.status
            }

            emitState(LoadingState(LoadingState.State.SUCCESS, REQUEST_RT_DATA))
        }
    }

    /**
     * stop requesting RT data
     */
    fun requestRTDataStop() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::requestRTDataStop()")
        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(STOP_RT_DATA)) {
            emitState(LoadingState(LoadingState.State.RUNNING, STOP_RT_DATA))

            mSmartMeterApi.smartMeterStopRTDataGet()

            emitState(LoadingState(LoadingState.State.SUCCESS, STOP_RT_DATA))
        }

        // stop hub connection
        if (mCloudSignalRRepository.getConnectionState() != HubConnectionState.DISCONNECTED) {
            mCloudSignalRRepository.stopConnection()
        }
        mExtendTimer.cancel()
        mExtendTimer = Timer()
    }

    /**
     * stop requesting RT data
     */
    fun requestRTDataExtend() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::requestRTDataExtend()")
        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(EXTEND_RT_DATA)) {
            emitState(LoadingState(LoadingState.State.RUNNING, EXTEND_RT_DATA))

            mSmartMeterApi.smartMeterExtendRTDataGet()

            emitState(LoadingState(LoadingState.State.SUCCESS, EXTEND_RT_DATA))
        }
    }

    /**
     * check connection to signal R
     */
    fun checkSignalRConnection() {
        mExtendTimer.schedule(object : TimerTask() {
            override fun run() {
                if (mCloudSignalRRepository.getConnectionState() != HubConnectionState.CONNECTED) {
                    Log.e(TAG, "$TAG_E_COMMUNITY_VM::lost SignalR connection")
                    emitState(LoadingState(LoadingState.State.FAILED))
                    mMeterDataRT.value = null

                    // start signalR
                    requestRTDataStart(false)
                }

                if (++mTimerCount == Constants.TIMER_COUNT) {
                    requestRTDataExtend()
                    mTimerCount = 0
                }

                checkSignalRConnection()
            }
        }, Constants.CHECK_SIGNALR_TIMER.toLong())
    }
    //endregion
}