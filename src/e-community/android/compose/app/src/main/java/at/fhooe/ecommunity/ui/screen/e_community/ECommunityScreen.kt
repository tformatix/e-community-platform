package at.fhooe.ecommunity.ui.screen.e_community

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.ui.component.DropDown
import at.fhooe.ecommunity.ui.component.LifecycleListener
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTopBar
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityPerformance
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityRealTime
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNewDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNonCompliance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityOffline
import java.util.*

/**
 * distribution application for eCommunities
 * @param viewModel view model for eCommunity screen
 * @see Composable
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ECommunityScreen(viewModel: ECommunityViewModel) {
    val runningOperations by remember { viewModel.mRunningOperations } // number of operations currently running (several possible at the same time)

    val eCommunity by remember { viewModel.mECommunity } // eCommunity data
    val smartMeters = remember { viewModel.mSmartMeters } // smart meters of member
    val selectedSmartMeterIdx by remember { viewModel.mSelectedSmartMeterIdx } // currently selected smart meter

    val meterDataRT by remember { viewModel.mMeterDataRT } // real time data

    val performance by remember { viewModel.mPerformance } // performance data of smart meter
    val currentPortion by remember { viewModel.mCurrentPortion } // current portion of smart meter

    val newDistribution by remember { viewModel.mNewDistribution } // new distribution available (e.g. between 11:55 and 12:00)
    val monitoringStatus = remember { viewModel.mMonitoringStatus } // monitoring anomalies (offline/non-compliance)

    LifecycleListener { event ->
        // be aware of lifecycle event
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                // register view model listener
                viewModel.registerListener { viewModelState ->
                    // handle state
                    when (viewModelState.mState) {
                        LoadingState.State.SUCCESS -> {
                            viewModel.mRunningOperations.value-- // increase running operations counter
                        }
                        LoadingState.State.FAILED -> {
                            // view model operation failed
                            viewModel.mRunningOperations.value-- // increase running operations counter
                            viewModelState.mException?.let {
                                val remoteException = viewModel.mApplication.remoteExceptionRepository.exceptionToRemoteException(it)
                                if (remoteException.mType != RemoteException.Type.NOT_FOUND) {
                                    // Output toast messages, except for not found exceptions
                                    Toast.makeText(
                                        viewModel.mApplication,
                                        viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(remoteException),
                                        Toast.LENGTH_SHORT
                                    ).show() // show error message
                                }
                            }
                        }
                        else -> {}
                    }
                }
                viewModel.initLoad() // load init data
                viewModel.requestRTDataStart(true) // request real time data (SignalR)
            }
            Lifecycle.Event.ON_PAUSE -> {
                viewModel.requestRTDataStop() // stop real time session
                viewModel.unregisterListener() // unregister view model listener
            }
            else -> {}
        }
    }

    LaunchedEffect(true) {
        // only execute once
        viewModel.checkSignalRConnection() // start SignalR connection checker
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            ECommunityTopBar(eCommunity, meterDataRT) // top bar
        }
    ) {
        if (runningOperations > 0) LoadingIndicator() // show loading indicator if there are running operations
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState()) // enable scrolling
                .padding(vertical = dimensionResource(id = R.dimen.activity_vertical_margin))
        ) {
            newDistribution?.let { _newDistribution ->
                // show new distribution if available
                ECommunityNewDistribution(
                    newDistribution = _newDistribution,
                    onAccepted = { portion, flexibility ->
                        // user acknowledged portion
                        viewModel.portionAck(portion, flexibility)
                    }
                )
            }
            monitoringStatus.distinct().forEach { _monitoringStatus ->
                // show monitoring status if available
                if (_monitoringStatus.projectedActiveEnergyPlus == null) {
                    // null means that smart meter is offline
                    ECommunityOffline(_monitoringStatus)
                } else {
                    // otherwise it is a non-compliance
                    ECommunityNonCompliance(
                        monitoringStatus = _monitoringStatus,
                        onToggleMute = {
                            viewModel.toggleMute(it)
                        }
                    )
                }
            }
            val smartMetersDistinct = smartMeters.distinct()
            if(smartMetersDistinct.size > 1) {
                // only show if there are multiple smart meters
                DropDown(
                    items = smartMetersDistinct.map { it.name ?: "" },
                    fontSize = 14.sp,
                    onSelected = { idx, _ ->
                        viewModel.mSelectedSmartMeterIdx.value = idx
                        viewModel.loadSmartMeterDependent()
                    }
                )
                ECommunityDivider() // horizontal line
            }
            // distribution performance
            ECommunityPerformance(
                performance = performance,
                onDurationDaysChanged = {
                    viewModel.mPerformanceDurationDays = it
                    viewModel.loadPerformance()
                }
            )
            ECommunityDivider() // horizontal line
            // hourly distribution
            ECommunityDistribution(
                currentPortion = currentPortion
            )
            ECommunityDivider() // horizontal line
            // real time monitoring
            ECommunityRealTime(
                meterDataRT = meterDataRT,
                selectedSmartMeterId = smartMeters.getOrNull(selectedSmartMeterIdx)?.id
            )
        }
    }
}