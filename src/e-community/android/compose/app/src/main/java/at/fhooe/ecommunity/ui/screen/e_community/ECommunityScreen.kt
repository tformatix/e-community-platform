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
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.ui.component.DropDown
import at.fhooe.ecommunity.ui.component.LifecycleListener
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTopBar
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityPerformance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNewDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNonCompliance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityOffline
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ECommunityScreen(viewModel: ECommunityViewModel, navController: NavHostController) {
    val runningOperations by remember { viewModel.mRunningOperations }

    val eCommunity by remember { viewModel.mECommunity }
    val smartMeters = remember { viewModel.mSmartMeters }

    val meterDataRT by remember { viewModel.mMeterDataRT }

    val performance by remember { viewModel.mPerformance }
    val currentPortion by remember { viewModel.mCurrentPortion }

    val newDistribution by remember { viewModel.mNewDistribution }
    val monitoringStatus = remember { viewModel.mMonitoringStatus }

    LifecycleListener {
        // be aware of lifecycle event
        when (it) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.registerListener { viewModelState ->
                    when (viewModelState.mState) {
                        LoadingState.State.SUCCESS -> {
                            viewModel.mRunningOperations.value--
                        }
                        LoadingState.State.FAILED -> {
                            // view model operation failed
                            viewModel.mRunningOperations.value--
                            viewModelState.mException?.let {
                                val remoteException = viewModel.mApplication.remoteExceptionRepository.exceptionToRemoteException(it)
                                if (remoteException.mType != RemoteException.Type.NOT_FOUND) {
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
                viewModel.initLoad()
                viewModel.requestRTDataStart(true)
            }
            Lifecycle.Event.ON_PAUSE -> {
                viewModel.requestRTDataStop()
                viewModel.unregisterListener()
            }
            Lifecycle.Event.ON_DESTROY -> {
                viewModel.requestRTDataStop()
                viewModel.unregisterListener()
            }
            else -> {}
        }
    }

    LaunchedEffect(true) {
        // only execute once
        viewModel.checkSignalRConnection()
    }

    Scaffold(
        modifier = Modifier
            .gesturesDisabled(runningOperations > 0)
            .fillMaxSize(),
        topBar = { ECommunityTopBar(eCommunity, meterDataRT) }
    ) {
        if (runningOperations > 0) LoadingIndicator()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = dimensionResource(id = R.dimen.activity_vertical_margin))
        ) {
            newDistribution?.let { _newDistribution ->
                ECommunityNewDistribution(
                    newDistribution = _newDistribution,
                    onAccepted = { portion, flexibility ->
                        viewModel.portionAck(portion, flexibility)
                    }
                )
            }
            monitoringStatus.forEach { monitoringStatus ->
                if (monitoringStatus.projectedActiveEnergyPlus == null) {
                    ECommunityOffline(monitoringStatus)
                } else {
                    ECommunityNonCompliance(
                        monitoringStatus = monitoringStatus,
                        onToggleMute = {
                            viewModel.toggleMute(it)
                        }
                    )
                }
            }
            if(smartMeters.size > 1) {
                // select smart meter
                DropDown(
                    items = smartMeters.map { it.name ?: "" },
                    fontSize = 14.sp,
                    onSelected = { idx, _ ->
                        viewModel.mSelectedSmartMeterIdx = idx
                        viewModel.loadSmartMeterDependent()
                    }
                )
                ECommunityDivider()
            }
            ECommunityPerformance(
                performance = performance,
                onDurationDaysChanged = {
                    viewModel.loadPerformance(it)
                }
            )
            ECommunityDivider()
            ECommunityDistribution(currentPortion)
        }
    }
}