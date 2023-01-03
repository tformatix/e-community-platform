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
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.ui.component.LifecycleListener
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTopBar
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityPerformance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNewDistribution
import at.fhooe.ecommunity.ui.screen.home.checkConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ECommunityScreen(viewModel: ECommunityViewModel, navController: NavHostController) {
    val runningOperations by remember { viewModel.mRunningOperations }
    val currentPortion by remember { viewModel.mCurrentPortion }
    val newDistribution by remember { viewModel.mNewDistribution }

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
                    if (!((viewModelState.mId == ECommunityViewModel.CURRENT || viewModelState.mId == ECommunityViewModel.NEW) &&
                                remoteException.mType == RemoteException.Type.NOT_FOUND)
                    ) {
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

    LifecycleListener {
        // be aware of lifecycle event
        if (it == Lifecycle.Event.ON_RESUME) {
            viewModel.init()
        }
    }

    Scaffold(
        modifier = Modifier
            .gesturesDisabled(runningOperations > 0)
            .fillMaxSize(),
        topBar = { ECommunityTopBar() }
    ) {
        if (runningOperations > 0) LoadingIndicator()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = dimensionResource(id = R.dimen.activity_vertical_margin))
        ) {
//            ECommunityOffline()
            newDistribution?.let { _newDistribution ->
                ECommunityNewDistribution(
                    newDistribution = _newDistribution,
                    onAccepted = { portion, flexibility ->
                        viewModel.portionAck(portion, flexibility)
                    }
                )
            }
//            ECommunityNonCompliance()
            ECommunityPerformance()
            ECommunityDivider()
            ECommunityDistribution(currentPortion)
        }
    }
}