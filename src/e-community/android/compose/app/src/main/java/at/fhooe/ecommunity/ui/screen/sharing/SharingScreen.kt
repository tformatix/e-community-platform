package at.fhooe.ecommunity.ui.screen.sharing

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.BlockchainAccountBalanceDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
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
            "", "", ""
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
        topBar = { TopBarHome(_navController) }
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
    }
    Log.e(TAG, _blockchainAccountBalance.toString())
}
