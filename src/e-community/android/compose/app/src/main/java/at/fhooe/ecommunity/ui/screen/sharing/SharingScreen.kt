package at.fhooe.ecommunity.ui.screen.sharing

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.local.entity.BlockchainBalance
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.screen.home.*
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractDataResolution
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractDataUsage
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractState
import at.fhooe.ecommunity.ui.screen.sharing.models.WalletCurrency
import at.fhooe.ecommunity.util.EncryptedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round
import kotlin.streams.toList

/**
 * Screen for Sharing (sell & buy energy data from other members)
 * @param _viewModel viewModel for SharingScreen
 * @param _navController navController for navigation to other Screens
 * @see Composable
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharingScreen(_viewModel: SharingViewModel, _navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    // saved as state to know when signalr sent the data
    val isLoading = remember {
        mutableStateOf(false)
    }

    // collect changes from the tileRepository
    val blockchainBalance: BlockchainBalance by _viewModel.getApplication().blockchainBalanceRepository.getBalance().collectAsState(initial = BlockchainBalance(received = "0", sent = "0", balance = "0"))

    // get address of me
    val addressLocal = remember { mutableStateOf("") }
    val sharedPrefs = EncryptedPreferences(_viewModel.getApplication())
    val memberId = sharedPrefs.getCredentials()?.memberId.toString()

    val loadingState by _viewModel.mLoadingConfig.collectAsState()

    // collect changes from the tileRepository
    val contracts: List<ConsentContract> by _viewModel.getApplication().contractRepository.getConsentContracts().collectAsState(initial = emptyList())

    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading.value = false
            _viewModel.backToIdle()
        }
        LoadingState.State.RUNNING -> {
            isLoading.value = true
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            _viewModel.backToIdle() // bring the view model back to the idle state
            Log.d(TAG, "error")

            if (state.mException == null) {
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(
                        RemoteException(RemoteException.Type.NO_INTERNET)
                    ),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
            state.mException?.let {
                val remoteException = _viewModel.mApplication.remoteExceptionRepository.exceptionToRemoteException(it)
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(
                        RemoteException(RemoteException.Type.NO_INTERNET)
                    ),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
        }
        else -> {}
    }

    LaunchedEffect(true) {
        isLoading.value = false
        addressLocal.value = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b"
        //_viewModel.getAddressForMember(memberId, addressLocal)
        //_viewModel.requestBlockchainAccountBalance(isLoading)
        //_viewModel.requestContractsForMember(isLoading)
        //_viewModel.requestHistoryData(isLoading)
        createTestContract(_viewModel)

        /*
        CoroutineScope(Dispatchers.Default).launch {
            _viewModel.mApplication.meterDataHistContractRepository.insert(
                MeterDataHistContract(
                    contractId = "e099e1d5-22fa-4baf-b18c-a51a4bfbcf00",
                    dataId = 5
                )
            )
        }
         */
    }

    // build screen (TopBar and GridLayout)
    Scaffold(
        topBar = { TopBarSharing(_navController) }
    ) {
        DashboardSharing(
            loadingState,
            contracts,
            _navController,
            addressLocal,
            blockchainBalance
        )
    }
}

fun createTestContract(_viewModel: SharingViewModel) {
    CoroutineScope(Dispatchers.Default).launch {
        //[{"ContractId": "e099e1d5-22fa-4baf-b18c-a51a4bfbcf00", "State": 4, "AddressProposer": "0xEA3576B983ab5270D60bF0FFF167aDC86075690b", "AddressConsenter": "0xEA3576B983ab5270D60bF0FFF167aDC86075690b", "StartEnergyData": "1674428400", "EndEnergyData": "1675119540", "ValidityOfContract": "1675205940", "PricePerHour": "1.0", "TotalPrice": "191.0"}]
        val contracts = listOf<ConsentContract>(
            ConsentContract(
                contractId = "e099e1d5-22fa-4baf-b18c-a51a4bfbcf00",
                state = ContractState.CONTRACT_ACTIVE_WITHDRAW_READY.ordinal,
                addressContract = null,
                addressProposer = "0xEA3576B983ab5270D60bF0FFF167aDC86075690a",
                addressConsenter = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b",
                startEnergyData = "1674428400",
                endEnergyData = "1675119540",
                validityOfContract = "1680186185",
                dataUsage = ContractDataUsage.CANDIDATE_E_COMMUNITY.ordinal,
                timeResolution = ContractDataResolution.TIME_1H.ordinal,
                pricePerHour = "1.54",
                totalPrice = "250"
            )/*,
            ConsentContract(
                contractId = "e099e1d5-22fa-4baf-b18c-a51a4bfbcf01",
                state = ContractState.CONTRACT_CLOSED.ordinal,
                addressContract = null,
                addressProposer = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b",
                addressConsenter = "0xEA3576B983ab5270D60bF0FFF167aDC86075690a",
                startEnergyData = "1674428400",
                endEnergyData = "1675119540",
                validityOfContract = "1675205940",
                dataUsage = ContractDataUsage.CANDIDATE_E_COMMUNITY.ordinal,
                timeResolution = ContractDataResolution.TIME_1H.ordinal,
                pricePerHour = "1.0",
                totalPrice = "191.0"
            ),
            ConsentContract(
                contractId = "e099e1d5-22fa-4baf-b18c-a51a4bfbcf02",
                state = ContractState.CONTRACT_CLOSED.ordinal,
                addressContract = null,
                addressProposer = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b",
                addressConsenter = "0xEA3576B983ab5270D60bF0FFF167aDC86075690a",
                startEnergyData = "1674428400",
                endEnergyData = "1675119540",
                validityOfContract = "1675205940",
                dataUsage = ContractDataUsage.CANDIDATE_E_COMMUNITY.ordinal,
                timeResolution = ContractDataResolution.TIME_1H.ordinal,
                pricePerHour = "1.0",
                totalPrice = "191.0"
            )
            ,
            ConsentContract(
                contractId = "e099e1d5-22fa-4baf-b18c-a51a4bfbcf03",
                state = ContractState.CONTRACT_PAYMENT_PENDING.ordinal,
                addressContract = null,
                addressProposer = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b",
                addressConsenter = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b",
                startEnergyData = "1674428400",
                endEnergyData = "1675119540",
                validityOfContract = "1675205940",
                dataUsage = ContractDataUsage.CANDIDATE_E_COMMUNITY.ordinal,
                timeResolution = ContractDataResolution.TIME_1H.ordinal,
                pricePerHour = "1.0",
                totalPrice = "191.0"
            )
            ,
            ConsentContract(
                contractId = "e099e1d5-22fa-4baf-b18c-a51a4bfbcf03",
                state = ContractState.CONTRACT_ACTIVE.ordinal,
                addressContract = null,
                addressProposer = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b",
                addressConsenter = "0xEA3576B983ab5270D60bF0FFF167aDC86075690a",
                startEnergyData = "1674428400",
                endEnergyData = "1675119540",
                validityOfContract = "1675205940",
                dataUsage = ContractDataUsage.FORECASTS.ordinal,
                timeResolution = ContractDataResolution.TIME_1H.ordinal,
                pricePerHour = "1.0",
                totalPrice = "2430"
            )
            */
        )
        _viewModel.mApplication.contractRepository.insert(contracts)
    }
}

@Composable
fun DashboardSharing(
    _isLoading: SharingConfig,
    _contracts: List<ConsentContract>,
    _navController: NavHostController,
    _addressLocal: MutableState<String>,
    _blockchainBalance: BlockchainBalance?
) {

    Box(modifier = Modifier.fillMaxSize()) {
        /*if (!_isLoading.hasBalanceLoaded && !_isLoading.hasContractsLoaded) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }*/

        Column {
            DashboardContracts(_contracts, _navController, _addressLocal)
            if (_blockchainBalance != null) {
                DashboardWallet(_blockchainBalance)
            }
        }
    }
}

@Composable
fun DashboardContracts(
    _contracts: List<ConsentContract>,
    _navController: NavHostController,
    _addressLocal: MutableState<String>
) {
    val backColor = if (isSystemInDarkTheme()) { Color.DarkGray } else { Color.LightGray }


    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp),
        shape = (RoundedCornerShape(15.dp)),
        elevation = 10.dp
    ) {
        Column(modifier = Modifier
            .padding(10.dp)) {
            Text(
                text = stringResource(R.string.sharing_contracts),
                style = MaterialTheme.typography.h4
            )

            Box(
                modifier = Modifier
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                // count of contract state
                var count = 0L

                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                            .clickable {
                                _navController.navigate("${Screen.SharingContractDetailsList.route}?selected_state=${ContractState.CONTRACT_ACTIVE.ordinal}")
                            }) {

                        count = _contracts.stream().filter { x -> x.state == ContractState.CONTRACT_ACTIVE.ordinal }.count()
                        ContractsCard(stringResource(R.string.sharing_contracts_active), count, R.drawable.ic_contracts_active)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                            .clickable {
                                _navController.navigate("${Screen.SharingContractDetailsList.route}?selected_state=${ContractState.CONTRACT_REQUESTS_FOR_ME.ordinal}")
                            }) {

                        count = _contracts.stream().filter {
                            x -> (x.addressConsenter?.uppercase() == _addressLocal.value.uppercase()
                                || x.addressProposer?.uppercase() == _addressLocal.value.uppercase())
                                &&
                                (x.state != ContractState.CONTRACT_CLOSED.ordinal
                                        && x.state != ContractState.CONTRACT_ACTIVE.ordinal
                                        && x.state != ContractState.CONTRACT_REJECTED.ordinal)
                        }.toList().count().toLong()

                        ContractsCard(stringResource(R.string.sharing_contracts_requests), count, R.drawable.ic_contracts_requests)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                            .clickable {
                                _navController.navigate("${Screen.SharingContractDetailsList.route}?selected_state=${ContractState.CONTRACT_PAYMENT_PENDING.ordinal}")
                            }) {

                        count = _contracts.stream().filter {
                                x -> x.addressConsenter?.uppercase() != _addressLocal.value.uppercase()
                                &&
                                x.state == ContractState.CONTRACT_PAYMENT_PENDING.ordinal
                        }.count()

                        ContractsCard(stringResource(R.string.sharing_contracts_pending), count, R.drawable.ic_contracts_pending)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()
                            .clickable {
                                _navController.navigate("${Screen.SharingContractDetailsList.route}?selected_state=${ContractState.CONTRACT_CLOSED.ordinal}")
                            }) {

                        count = _contracts.stream().filter { x -> x.state == ContractState.CONTRACT_CLOSED.ordinal }.count()
                        ContractsCard(stringResource(R.string.sharing_contracts_closed), count, R.drawable.ic_contracts_saved)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardWallet(_blockchainAccountBalance: BlockchainBalance) {
    val backColor = if (isSystemInDarkTheme()) { Color.DarkGray } else { Color.LightGray }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp),
        shape = (RoundedCornerShape(15.dp)),
        elevation = 10.dp
    ) {
        Column(modifier = Modifier
                .padding(10.dp)) {
            Text(
                text = stringResource(R.string.sharing_wallet),
                style = MaterialTheme.typography.h4
            )

            val currencyEthereum = WalletCurrency(
                name = stringResource(R.string.sharing_wallet_currency_ethereum),
                iconId = R.drawable.ic_eth,
                iconDescription = stringResource(R.string.sharing_wallet_currency_ethereum)
            )

            WalletCurrencyCard(currencyEthereum)

            Box(
                modifier = Modifier
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row {
                    /*Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        WalletCard(stringResource(R.string.sharing_wallet_received), _blockchainAccountBalance.received)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        WalletCard(stringResource(R.string.sharing_wallet_sent), _blockchainAccountBalance.sent)
                    }*/

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        WalletCard(stringResource(R.string.sharing_wallet_balance), _blockchainAccountBalance.balance)
                    }
                }
            }
        }
    }
}

@Composable
fun WalletCurrencyCard(
    _walletCurrency: WalletCurrency
) {

    Card(modifier = Modifier
        .clip(RoundedCornerShape(10.dp)),
        elevation = 6.dp) {
        Row(horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(_walletCurrency.iconId),
                modifier = Modifier
                    .padding(top = 3.dp)
                    .height(20.dp)
                    .width(20.dp),
                contentDescription = _walletCurrency.iconDescription,
            )

            Text(
                modifier = Modifier.padding(end = 3.dp),
                text = _walletCurrency.name,
                style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
fun WalletCard(_action: String, _value: String?) {
    Text(
        text = _action,
        textAlign = TextAlign.Center
    )

    _value?.let {
        Log.e(TAG, _value)
        //val rounded = DecimalFormat("0.000").format(it.toFloat())

        val rounded = BigDecimal(_value.toDouble()).setScale(4, RoundingMode.HALF_UP)

        Text(
            text = "$rounded ETH",
            style = MaterialTheme.typography.h5,
            color = colorResource(R.color.value_good)
        )
    }

    /*Image(
        painter = painterResource(R.drawable.ic_eth),
        modifier = Modifier.height(50.dp),
        contentDescription = stringResource(R.string.sharing_wallet_eth),
    )*/
}

@Composable
fun ContractsCard(_action: String, _value: Long, _iconId: Int) {
    Text(
        text = _action,
        textAlign = TextAlign.Center
    )

    Text(
        text = _value.toString(),
        style = MaterialTheme.typography.h5
    )

    Image(
        painter = painterResource(_iconId),
        contentDescription = stringResource(R.string.sharing_wallet_eth),
    )
}

/**
 * topBar has a switch for realtime/history and a filter function
 * @see Composable
 */
@Composable
fun TopBarSharing(_navController: NavHostController) {

    TopAppBar(
        title = { Text("Sharing")},
        actions = { }
    )
}