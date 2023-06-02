package at.fhooe.ecommunity.ui.screen.sharing

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.local.entity.BlockchainBalance
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.data.local.entity.MeterDataHistContract
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.BlockchainApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.HistoryApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.BlockchainAccountBalanceDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ConsentContractModel
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.RequestHistoryModel
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.UpdateContractState
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractState
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractUpdateState
import at.fhooe.ecommunity.util.ECommunityFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONObject
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * viewModel for Sharing Screen
 * @param _application application object
 * interaction with
 * @see CloudSignalRRepository
 */
class SharingViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {

    // signal r repo
    private var mCloudSignalRRepository = CloudSignalRRepository()

    var mLoadingConfig = MutableStateFlow(
        SharingConfig(
            hasBalanceLoaded = false,
            hasContractsLoaded = false
        )
    )

    /**
     * requests blockchain account balance from local blockchain node
     * @param _isLoading used to know when the data arrived
     * @param _blockchainAccountBalance blockchain account balance dto
     */
    fun requestBlockchainAccountBalance(
        _isLoading: MutableState<Boolean>
    ) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                //mState.emit(LoadingState(LoadingState.State.RUNNING))

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainGetAccountBalanceGet()

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_BLOCK_ACC_BALANCE, null)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    fun updateContract(
        _contract: ConsentContract,
        _updateState: Int
    ) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                //mState.emit(LoadingState(LoadingState.State.RUNNING))

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainUpdateContractStatePost(
                        UpdateContractState(_contract.contractId, _updateState)
                    )

                    // update state in room db
                    _contract.state = when(_updateState) {
                        ContractUpdateState.CONTRACT_ACCEPT.ordinal -> {
                            ContractState.CONTRACT_PAYMENT_PENDING.ordinal
                        }
                        ContractUpdateState.CONTRACT_REJECT.ordinal -> {
                            ContractState.CONTRACT_REJECTED.ordinal
                        }
                        ContractUpdateState.CONTRACT_REVOKE.ordinal -> {
                            ContractState.CONTRACT_REVOKED.ordinal
                        }
                        else -> {
                            ContractState.CONTRACT_CLOSED.ordinal
                        }
                    }

                    CoroutineScope(Dispatchers.Default).launch {
                        this@SharingViewModel.mApplication.contractRepository.update(
                            _contract
                        )
                    }
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    fun requestContractsForMember(_isLoading: MutableState<Boolean>) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainGetContractsForMemberGet()

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_CONTRACTS_FOR_MEMBER, null)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    fun requestHistoryData(_isLoading: MutableState<Boolean>, _contract: ConsentContract) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                //mState.emit(LoadingState(LoadingState.State.RUNNING))

                try {
                    val historyApi = HistoryApi(Constants.HTTP_BASE_URL_CLOUD)

                    val fromTimestamp1 = ECommunityFormatter(mApplication)
                        .convertUnixTimestampToOffsetDateTime(_contract.startEnergyData.toString().toLong(), true)

                    /*
                    val toTimestamp = ECommunityFormatter(mApplication)
                        .convertUnixTimestampToOffsetDateTime(_contract.endEnergyData.toString().toLong(), false)
                    */

                    val fromTimestamp = OffsetDateTime
                        .parse("2023-01-23T06:00:00.000Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .atZoneSameInstant(ZoneId.systemDefault()) // move to device's time zone
                        .toOffsetDateTime()

                    Log.e(TAG, "from1: ${fromTimestamp1}")
                    Log.e(TAG, "from2: $fromTimestamp")

                    val toTimestamp = OffsetDateTime
                        .parse("2023-01-23T15:00:00.000Z", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .atZoneSameInstant(ZoneId.systemDefault()) // move to device's time zone
                        .toOffsetDateTime()

                    // tobi bac8e613-83e5-4a23-44af-08da17d5b27d
                    // michi 1f302407-9258-4ef2-a474-08da3744278c
                    historyApi.historyGetHistoryPost(RequestHistoryModel(
                        requestedMemberId = UUID.fromString("1f302407-9258-4ef2-a474-08da3744278c"),
                        fromTimestamp = fromTimestamp,
                        toTimestamp = toTimestamp,
                        timeResolution = 30
                    ))

                    Log.d(TAG, "request history data for contractId: ${_contract.contractId}")

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_HISTORY_DATA, null, _contract)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    fun getAddressForMember(_memberId: String, _accountAddress: MutableState<String>) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    _accountAddress.value = blockchainApi.blockchainGetBlockchainAddressForMemberPost(_memberId)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }

    /**
     * create a new consent contract on the blockchain
     * @param _isLoading used for waiting screen
     * @param _consentContract model of consent contract
     */
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

                try {
                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainCreateConsentContractPost(_consentContract.value)

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_CREATE_CON_CONTRACT, _consentContract)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                }
            }
        }
    }

    /**
     * deposit the totalPrice to the contract wallet
     * @param _isLoading used for waiting screen
     */
    fun depositToContract(
        _isLoading: MutableState<Boolean>,
        _consentContract: ConsentContract
    ) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val contractModel = ConsentContractModel(
                        contractId = _consentContract.contractId,
                        state = _consentContract.state,
                        addressContract = _consentContract.addressContract,
                        addressProposer = _consentContract.addressProposer,
                        addressConsenter = _consentContract.addressConsenter,
                        startEnergyData = _consentContract.startEnergyData,
                        endEnergyData = _consentContract.endEnergyData,
                        validityOfContract = _consentContract.validityOfContract,
                        pricePerHour = _consentContract.pricePerHour,
                        totalPrice = _consentContract.totalPrice
                    )

                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainDepositToContractPost(contractModel)

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_DEPOSIT_TO_CONTRACT, null)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                }
            }
        }
    }

    /**
     * withdraws the totalPrice from the contract wallet to the consenters wallet
     * @param _isLoading used for waiting screen
     */
    fun withdrawFromContract(
        _isLoading: MutableState<Boolean>,
        _consentContract: ConsentContract
    ) {
        val cloudRESTRepository = mApplication.cloudRESTRepository

        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "CoroutineExceptionHandler got $exception")
        }
        cloudRESTRepository.authorizedBackendCall(handler) { token ->
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val contractModel = ConsentContractModel(
                        contractId = _consentContract.contractId,
                        state = _consentContract.state,
                        addressContract = _consentContract.addressContract,
                        addressProposer = _consentContract.addressProposer,
                        addressConsenter = _consentContract.addressConsenter,
                        startEnergyData = _consentContract.startEnergyData,
                        endEnergyData = _consentContract.endEnergyData,
                        validityOfContract = _consentContract.validityOfContract,
                        pricePerHour = _consentContract.pricePerHour,
                        totalPrice = _consentContract.totalPrice
                    )

                    val blockchainApi = BlockchainApi(Constants.HTTP_BASE_URL_CLOUD)
                    blockchainApi.blockchainWithdrawFromContractPost(contractModel)

                    startSignalR(_isLoading, token, Constants.SIGNALR_METHOD_WITHDRAW_FROM_CONTRACT, null)
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
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
     * @param _object this method can have multiple states
     */
    @SuppressLint("SuspiciousIndentation")
    private fun startSignalR(
        _isLoading: MutableState<Boolean>,
        _accessToken: String,
        _method: String,
        _object: MutableState<*>?,
        _consentContract: ConsentContract? = null
    ) {
        val connStr = Constants.HTTP_BASE_URL_CLOUD + Constants.SIGNALR_URL

        mCloudSignalRRepository = CloudSignalRRepository()

        when (_method) {
            Constants.SIGNALR_METHOD_BLOCK_ACC_BALANCE -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_BLOCK_ACC_BALANCE,
                    Action1 { data -> run {
                            val json = JSONObject(data.toMap())

                            val accountBalance = Gson().fromJson(
                                json.toString(),
                                BlockchainAccountBalanceDto::class.java)

                                // update balance in room db
                                CoroutineScope(Dispatchers.Default).launch {
                                    this@SharingViewModel.mApplication.blockchainBalanceRepository.insert(
                                        BlockchainBalance(
                                            received = accountBalance.received,
                                            sent = accountBalance.sent,
                                            balance = accountBalance.balance
                                        )
                                    )
                                }

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

                            if (_object?.value is ConsentContractModel) {
                                (_object as MutableState<ConsentContractModel>).value = Gson().fromJson(
                                    json.toString(),
                                    ConsentContractModel::class.java
                                )

                                _isLoading.value = false
                            }

                            mCloudSignalRRepository.stopConnection()
                        }
                    }
                )
            }

            Constants.SIGNALR_METHOD_CONTRACTS_FOR_MEMBER -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_CONTRACTS_FOR_MEMBER,
                    Action1 { data -> run {
                            val json = JSONObject(data.toMap())

                            try {

                                val itemType = object : TypeToken<List<ConsentContract>>() {}.type
                                val contracts = Gson().fromJson<List<ConsentContract>>(json.get("contracts").toString(), itemType)

                                // update contracts in room db
                                CoroutineScope(Dispatchers.Default).launch {
                                    this@SharingViewModel.mApplication.contractRepository.insert(contracts)

                                    mLoadingConfig.value = mLoadingConfig.value.copy(
                                        hasBalanceLoaded = mLoadingConfig.value.hasBalanceLoaded,
                                        hasContractsLoaded = true
                                    )
                                    _isLoading.value = false
                                }

                                mCloudSignalRRepository.stopConnection()
                            }
                            catch (_e: Exception) {
                                Log.e(TAG, _e.toString())
                            }
                        }
                    }
                )
            }

            Constants.SIGNALR_METHOD_HISTORY_DATA -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_HISTORY_DATA,
                    Action1 { data -> run {
                        val json = JSONObject(data.toMap())

                        val itemType = object : TypeToken<List<MeterDataHistContract>>() {}.type
                        val contracts = Gson().fromJson<List<MeterDataHistContract>>(json.get("meterDataValues").toString(), itemType)

                        // set contractId to values
                        contracts.forEach {
                            x -> x.contractId = _consentContract?.contractId.toString()
                        }

                        CoroutineScope(Dispatchers.Default).launch {
                            this@SharingViewModel.mApplication.meterDataHistContractRepository.insert(contracts)
                            _isLoading.value = false
                        }


                        mCloudSignalRRepository.stopConnection()
                    }
                    }
                )
            }

            Constants.SIGNALR_METHOD_DEPOSIT_TO_CONTRACT -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_DEPOSIT_TO_CONTRACT,
                    Action1 { data -> run {
                        val json = JSONObject(data.toMap())
                        Log.e(TAG, "deposit: $json")

                        _isLoading.value = false
                    }
                    }
                )
            }

            Constants.SIGNALR_METHOD_WITHDRAW_FROM_CONTRACT -> {
                mCloudSignalRRepository.initialize(connStr, _accessToken, Constants.SIGNALR_METHOD_WITHDRAW_FROM_CONTRACT,
                    Action1 { data -> run {
                        val json = JSONObject(data.toMap())
                        Log.e(TAG, "withdraw: $json")
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

    /**
     * get the application object
     */
    fun getApplication() = mApplication
}

data class SharingConfig(var hasBalanceLoaded: Boolean, var hasContractsLoaded: Boolean)