package at.fhooe.ecommunity.ui.screen.sharing

import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.BlockchainApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.BlockchainAccountBalanceDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ConsentContractModel
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import com.google.gson.Gson
import com.microsoft.signalr.Action1
import kotlinx.coroutines.*
import org.json.JSONObject

/**
 * viewModel for Sharing Screen
 * @param _application application object
 * interaction with
 * @see CloudSignalRRepository
 */
class SharingViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {

    // signal r repo
    private var mCloudSignalRRepository = CloudSignalRRepository()

    /**
     * requests blockchain account balance from local blockchain node
     * @param _isLoading used to know when the data arrived
     * @param _blockchainAccountBalance blockchain account balance dto
     */
    fun requestBlockchainAccountBalance(
        _isLoading: MutableState<Boolean>,
        _blockchainAccountBalance: MutableState<BlockchainAccountBalanceDto>
    ) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainGetAccountBalanceGet()

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_BLOCK_ACC_BALANCE, _blockchainAccountBalance, null)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    fun createConsentContract(
        _isLoading: MutableState<Boolean>,
        _consentContract: MutableState<ConsentContractModel>
    ) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainCreateConsentContractPost(_consentContract.value)

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_CREATE_CON_CONTRACT, null, _consentContract)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    /**
     * start signal r listener and wait for blockchainAccountBalance
     * SharingScreen gets notified when data changes
     * @param _isLoading used to know when the data arrived
     * @param _method different signal r methods
     * @param _accessToken for signalr authentication
     * @param _blockchainAccountBalance balance of the blockchain account
     */
    private fun startSignalR(
        _isLoading: MutableState<Boolean>,
        _accessToken: String,
        _method: String,
        _blockchainAccountBalance: MutableState<BlockchainAccountBalanceDto>?,
        _consentContract: MutableState<ConsentContractModel>?
    ) {
        val connStr = Constants.HTTP_BASE_URL_CLOUD + Constants.SIGNALR_URL

        mCloudSignalRRepository = CloudSignalRRepository()

        when (_method) {
            Constants.SIGNALR_METHOD_BLOCK_ACC_BALANCE -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_BLOCK_ACC_BALANCE,
                    Action1 { data -> run {
                            val json = JSONObject(data.toMap())
                            _blockchainAccountBalance?.value = Gson().fromJson(json.toString(), BlockchainAccountBalanceDto::class.java)
                            _isLoading.value = false
                            mCloudSignalRRepository.stopConnection()
                        }
                    }
                )
            }

            Constants.SIGNALR_METHOD_CREATE_CON_CONTRACT -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_CREATE_CON_CONTRACT,
                    Action1 { data -> run {
                            val json = JSONObject(data.toMap())
                            Log.d(TAG, "signal r received ConsentContract: ${json}")
                            _consentContract?.value = Gson().fromJson(json.toString(), ConsentContractModel::class.java)
                            _isLoading.value = false
                            mCloudSignalRRepository.stopConnection()
                        }
                    }
                )
            }
        }

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
}