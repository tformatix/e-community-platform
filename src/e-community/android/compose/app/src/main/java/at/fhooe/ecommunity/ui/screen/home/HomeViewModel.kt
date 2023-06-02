package at.fhooe.ecommunity.ui.screen.home

import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SmartMeterApi
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.data.remote.signalr.dto.MeterDataRTDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import com.google.gson.Gson
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * viewModel for Home Screen
 * @param _application application object
 * interaction with
 * @see CloudSignalRRepository
 * @see CloudSignalRRepository
 */
class HomeViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {

    // signal r repo
    var mCloudSignalRRepository = CloudSignalRRepository()

    /**
     * start signal r listener, with state of meterDataRto
     * HomeScreen gets notified when data changes
     */
    private fun startSignalR(_accessToken: String, _meterDataRTDto: MutableState<BufferedMeterDataRTDto>) {
        val connStr = Constants.HTTP_BASE_URL_CLOUD + Constants.SIGNALR_URL

        mCloudSignalRRepository = CloudSignalRRepository()

        mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD,
            Action1 { data -> run {
                val json = JSONObject(data.toMap()) // json object of server data
                _meterDataRTDto.value = Gson().fromJson(json.toString(), BufferedMeterDataRTDto::class.java)
                Log.d(TAG, _meterDataRTDto.value.toString())
            }
        })

        // start connection
        mCloudSignalRRepository.let {
            val started = it.startConnection()

            if (started) {
                Log.d(TAG, "startSignalR() SignalR try starting")
            } else {
                Log.e(TAG, "startSignalR() SignalR not started")
            }
        }
    }

    /**
     * request RT data from cloud
     * @param _meterDataRTDto store state of the current RT meterData
     */
    fun requestRTDataStart(_meterDataRTDto: MutableState<BufferedMeterDataRTDto>, _requestStart: Boolean = true) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                // start signal r
                startSignalR(token, _meterDataRTDto)

                if (_requestStart) {
                    val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                    try {
                        val start = smartMeterApi.smartMeterRequestRTDataGet()

                        Log.d(TAG, start.toString())
                        mState.emit(LoadingState(LoadingState.State.SUCCESS))
                    }
                    catch (_e: Exception) {
                        Log.e(TAG, _e.toString())
                        mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                    }
                }
                else {
                    mState.emit(LoadingState(LoadingState.State.SUCCESS))
                }
            }
        }
    }

    /**
     * stop requesting RT data
     */
    fun requestRTDataStop() {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) {

            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))
                val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                try {
                    val stop = smartMeterApi.smartMeterStopRTDataGet()

                    mState.emit(LoadingState(LoadingState.State.SUCCESS))
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }

        // stop hub connection
        if (mCloudSignalRRepository.getConnectionState() != HubConnectionState.DISCONNECTED) {
            mCloudSignalRRepository.stopConnection()
        }
    }

    /**
     * stop requesting RT data
     */
    fun requestRTDataExtend() {
        Log.d(TAG, "requestExtend")
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) {

            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))
                val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                try {
                    val extend = smartMeterApi.smartMeterExtendRTDataGet()

                    mState.emit(LoadingState(LoadingState.State.SUCCESS))
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    fun getConnectionState(): HubConnectionState {
        return mCloudSignalRRepository.getConnectionState()
    }

    /**
     * get the application object
     */
    fun getApplication() = mApplication
}