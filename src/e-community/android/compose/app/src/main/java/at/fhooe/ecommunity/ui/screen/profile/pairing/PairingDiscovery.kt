package at.fhooe.ecommunity.ui.screen.profile.pairing

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.Local
import at.fhooe.ecommunity.ui.component.LoadingIndicator

/**
 * discover local devices using multicast DNS (mDNS)
 * @param _viewModel viewModel for Pairing Workflow
 * @param _navController navController for navigation to other Screens of pairing
 * @see Composable
 */
@Composable
fun PairingDiscovery(_viewModel: PairingViewModel, _navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()
    val localDevice = remember { mutableStateOf(Local("", ""))}
    val localDeviceList = MutableList<Local>(init = { Local("", "") }, size = 0)

    var isLoading = true

    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading = false
            _viewModel.backToIdle()
        }
        LoadingState.State.RUNNING -> {
            LoadingIndicator()
            isLoading = true
        }
        LoadingState.State.FAILED -> {
            state.mException?.let {
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.exceptionToString(it),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
        }
        else -> {}
    }

    // when a device is found add it to the list
    if (localDevice.value.ipAddress != "") {
        localDeviceList.add(localDevice.value)
    }

    // build screen (TopBar and list of discovery devices)
    Scaffold(
        topBar = { TopBarPairingDiscovery() }
    ) {
        DeviceDiscovery(isLoading, localDeviceList)
        _viewModel.discoverDevices(localDevice)
    }
}

/**
 * display a list of discovered devices (via mDNS)
 * @param _isLoading check if page is loading
 * @param _localDeviceList discovered devices
 * @see Composable
 */
@Composable
fun DeviceDiscovery(_isLoading: Boolean, _localDeviceList: MutableList<Local>) {

    Box(
        modifier = Modifier
            .gesturesDisabled(_isLoading)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // list of current smart meters
            LazyColumn {
                items(_localDeviceList) { item ->
                    LocalDevice(item)
                }
            }
        }
    }
}

/**
 * display a single local devices
 * @param _item discovered device
 * @see Composable
 */
@Composable
fun LocalDevice(_item: Local) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        // name
        Text(
            text = _item.deviceName,
            fontSize = 16.sp
        )

        // ip-address
        Text(
            text = _item.ipAddress,
            fontSize = 12.sp
        )
    }
}

/**
 * at the moment just display a text
 * @see Composable
 */
@Composable
fun TopBarPairingDiscovery() {

    TopAppBar(
        title = { Text(text = LocalContext.current.getString(R.string.pairing_discovery_title)) }
    )
}