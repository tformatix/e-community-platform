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
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import com.google.gson.Gson
import com.microsoft.signalr.HubConnectionState
import org.json.JSONObject
import java.util.*

/**
 * view model for the "ECommunityScreen" and is a singleton
 * @param _application eCommunity application
 * @see LoadingStateViewModel
 */
class ECommunityViewModel private constructor(_application: ECommunityApplication) : LoadingStateViewModel(_application) {
    companion object {
        const val TAG_E_COMMUNITY_VM = "ECommunityViewModel"

        // loading state id's
        const val BASE_DATA = 0
        const val PERFORMANCE = 1
        const val CURRENT = 2
        const val NEW = 3
        const val MONITORING = 4
        const val PORTION_ACK = 5
        const val REQUEST_RT_DATA = 6
        const val STOP_RT_DATA = 7
        const val EXTEND_RT_DATA = 8

        private var instance: ECommunityViewModel? = null // singleton instance

        /**
         * @param _application eCommunity application
         * @return singleton instance of view model
         */
        fun getInstance(_application: ECommunityApplication): ECommunityViewModel {
            if (instance == null)
                instance = ECommunityViewModel(_application)
            return instance!!
        }
    }

    private var mCloudSignalRRepository = CloudSignalRRepository() // SignalR repository

    // API classes
    private val mECommunityApi = ECommunityApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mSmartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mDistributionApi = DistributionApi(Constants.HTTP_BASE_URL_CLOUD)
    private val mMonitoringApi = MonitoringApi(Constants.HTTP_BASE_URL_CLOUD)

    var mRunningOperations = mutableStateOf(0) // number of operations currently running (several possible at the same time)

    val mSmartMeters = mutableStateListOf<MinimalSmartMeterDto>() // smart meters of member
    val mSelectedSmartMeterIdx = mutableStateOf(0) // currently selected smart meter
    val mECommunity = mutableStateOf<MinimalECommunityDto?>(null) // eCommunity data

    val mMeterDataRT = mutableStateOf<BufferedMeterDataRTDto?>(null) // real time data
    private var mExtendTimer = Timer() // timer for extending real time sessions
    private var mTimerCount = 0 // timer is set to 10 seconds and the rt-session is extended after the timer has run 30 times

    val mPerformance = mutableStateOf<PerformanceDto?>(null) // performance data of smart meter
    var mPerformanceDurationDays = Constants.DEFAULT_PERFORMANCE_DURATION_DAYS // performance period under review
    val mCurrentPortion = mutableStateOf<CurrentPortionDto?>(null) // current portion of smart meter

    val mNewDistribution = mutableStateOf<NewDistributionDto?>(null) // new distribution available (e.g. between 11:55 and 12:00)
    val mMonitoringStatus = mutableStateListOf<MonitoringStatusDto>() // monitoring anomalies (offline/non-compliance)

    //region REST
    /**
     * loads all data at init
     */
    fun initLoad() {
        mRunningOperations.value = 0
        loadBaseData()
        loadNewDistribution()
        loadMonitoringStatus()
    }

    /**
     * loads base data
     */
    private fun loadBaseData() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadBaseData()")

        mRunningOperations.value++
        mECommunity.value = null // reset eCommunity
        mSmartMeters.clear() // reset smart meters

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(BASE_DATA)) {
            emitState(LoadingState(LoadingState.State.RUNNING, BASE_DATA))

            mECommunity.value = mECommunityApi.eCommunityGetMinimalECommunityGet() // load eCommunity
            mSmartMeters.addAll(mSmartMeterApi.smartMeterGetMinimalSmartMetersGet()) // load smart meters

            loadSmartMeterDependent()

            emitState(LoadingState(LoadingState.State.SUCCESS, BASE_DATA))
        }
    }

    /**
     * loads smart meter dependent data
     */
    fun loadSmartMeterDependent() {
        loadPerformance()
        loadCurrentPortion()
    }

    /**
     * loads performance data
     */
    fun loadPerformance() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadPerformance($mPerformanceDurationDays)")

        val smartMeterId = mSmartMeters.getOrNull(mSelectedSmartMeterIdx.value)?.id // id of selected smart meter
        if (smartMeterId != null) {
            mRunningOperations.value++
            mPerformance.value = null // reset performance

            mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PERFORMANCE)) {
                emitState(LoadingState(LoadingState.State.RUNNING, PERFORMANCE))

                mPerformance.value = mMonitoringApi.monitoringPerformanceGet(smartMeterId, mPerformanceDurationDays)

                emitState(LoadingState(LoadingState.State.SUCCESS, PERFORMANCE))
            }
        }
    }

    /**
     * loads current portion
     */
    private fun loadCurrentPortion() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadCurrentPortion()")

        val smartMeterId = mSmartMeters.getOrNull(mSelectedSmartMeterIdx.value)?.id // id of selected smart meter
        if (smartMeterId != null) {
            mRunningOperations.value++
            mCurrentPortion.value = null // reset current portion

            mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(CURRENT)) {
                emitState(LoadingState(LoadingState.State.RUNNING, CURRENT))

                mCurrentPortion.value = mDistributionApi.distributionCurrentPortionGet(smartMeterId)

                emitState(LoadingState(LoadingState.State.SUCCESS, CURRENT))
            }
        }
    }

    /**
     * loads new distribution
     */
    private fun loadNewDistribution() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadNewDistribution()")

        mRunningOperations.value++
        mNewDistribution.value = null // reset new distribution

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(NEW)) {
            emitState(LoadingState(LoadingState.State.RUNNING, NEW))

            mNewDistribution.value = mDistributionApi.distributionNewDistributionGet()

            emitState(LoadingState(LoadingState.State.SUCCESS, NEW))
        }
    }

    /**
     * loads monitoring status (offline/non-compliance)
     */
    private fun loadMonitoringStatus() {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::loadMonitoringStatus()")
        mRunningOperations.value++
        mMonitoringStatus.clear() // reset monitoring status

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(MONITORING)) {
            emitState(LoadingState(LoadingState.State.RUNNING, MONITORING))

            mMonitoringStatus.addAll(mMonitoringApi.monitoringMonitoringStatusGet())

            emitState(LoadingState(LoadingState.State.SUCCESS, MONITORING))
        }
    }

    /**
     * acknowledge portion
     * @param _newPortion acknowledged portion
     * @param _flexibility acknowledged flexibility
     */
    fun portionAck(_newPortion: NewPortionDto, _flexibility: Int) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::portionAck()")

        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))

            mDistributionApi.distributionHourlyPortionAckPost(PortionAckModel(_newPortion.smartMeterId, _flexibility))
            initLoad() // reload everything (because of re-distribution)

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }

    /**
     * toggle non-compliance muting (for current hour)
     */
    fun toggleMute(_smartMeterId: UUID) {
        Log.d(TAG, "$TAG_E_COMMUNITY_VM::muteCurrentHourMonitoring($_smartMeterId)")

        mRunningOperations.value++

        mApplication.cloudRESTRepository.authorizedBackendCall(getDefaultExceptionHandler(PORTION_ACK)) {
            emitState(LoadingState(LoadingState.State.RUNNING, PORTION_ACK))

            mMonitoringApi.monitoringToggleMuteCurrentHourGet(_smartMeterId)
            initLoad() // reload everything

            emitState(LoadingState(LoadingState.State.SUCCESS, PORTION_ACK))
        }
    }
    //endregion

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
                mSmartMeterApi.smartMeterRequestRTDataGet()
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
     * extend RT data timer
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
     * check SignalR connection (interval of 10 seconds)
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
                    // after 5 minutes (30 * 10 seconds)
                    requestRTDataExtend()
                    mTimerCount = 0
                }

                checkSignalRConnection()
            }
        }, Constants.CHECK_SIGNALR_TIMER.toLong())
    }
    //endregion
}