package at.fhooe.ecommunity.ui.screen.sharing

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.BlockchainAccountBalanceDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.screen.home.*

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

    // save state of blockchain account balance
    val blockchainAccountBalance = remember {
        mutableStateOf(BlockchainAccountBalanceDto(
            "0", "0", "0"
        ))
    }

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
        isLoading.value = true
        _viewModel.requestBlockchainAccountBalance(isLoading, blockchainAccountBalance)
    }

    // build screen (TopBar and GridLayout)
    Scaffold(
        topBar = { TopBarSharing(_navController) }
    ) {
        DashboardSharing(isLoading, blockchainAccountBalance)
    }
}

@Composable
fun DashboardSharing(_isLoading: MutableState<Boolean>, _blockchainAccountBalance: MutableState<BlockchainAccountBalanceDto>) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (_isLoading.value) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column {
            DashboardContracts()
            DashboardWallet(_blockchainAccountBalance)
        }
    }
    Log.e(TAG, _blockchainAccountBalance.toString())
}

@Composable
fun DashboardContracts() {
    val backColor = if (isSystemInDarkTheme()) { Color.DarkGray } else { Color.LightGray }

    Box(modifier = Modifier
        .padding(top = 10.dp)
        .clip(RoundedCornerShape(15.dp))
        .fillMaxWidth()
        .background(backColor)

    ) {
        Column(modifier = Modifier
            .padding(10.dp)) {
            Text(
                text = stringResource(R.string.sharing_contracts),
                style = MaterialTheme.typography.h3
            )

            Box(
                modifier = Modifier
                    .padding(top = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        ContractsCard(stringResource(R.string.sharing_contracts_active), "2", R.drawable.ic_contracts_active)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        ContractsCard(stringResource(R.string.sharing_contracts_requests), "0", R.drawable.ic_contracts_requests)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        ContractsCard(stringResource(R.string.sharing_contracts_pending), "3", R.drawable.ic_contracts_pending)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        ContractsCard(stringResource(R.string.sharing_contracts_closed), "8", R.drawable.ic_contracts_saved)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardWallet(_blockchainAccountBalance: MutableState<BlockchainAccountBalanceDto>) {
    val backColor = if (isSystemInDarkTheme()) { Color.DarkGray } else { Color.LightGray }

    Box(modifier = Modifier
        .padding(top = 10.dp)
        .clip(RoundedCornerShape(15.dp))
        .fillMaxWidth()
        .background(backColor)

        ) {
        Column(modifier = Modifier
                .padding(10.dp)) {
            Text(
                text = stringResource(R.string.sharing_wallet),
                style = MaterialTheme.typography.h3
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        WalletCard(stringResource(R.string.sharing_wallet_received), _blockchainAccountBalance.value.received)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        WalletCard(stringResource(R.string.sharing_wallet_sent), _blockchainAccountBalance.value.sent)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1.0f)
                            .fillMaxWidth()) {

                        _blockchainAccountBalance.value.balance = "0.54"
                        WalletCard(stringResource(R.string.sharing_wallet_balance), _blockchainAccountBalance.value.balance)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
        Text(
            text = it,
            style = MaterialTheme.typography.h3
        )
    }

    /*Image(
        painter = painterResource(R.drawable.ic_eth),
        modifier = Modifier.height(50.dp),
        contentDescription = stringResource(R.string.sharing_wallet_eth),
    )*/
}

@Composable
fun ContractsCard(_action: String, _value: String?, _iconId: Int) {
    Text(
        text = _action,
        textAlign = TextAlign.Center
    )

    _value?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.h3
        )
    }

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