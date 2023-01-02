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
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTopBar
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityPerformance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNewDistribution
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ECommunityScreen(viewModel: ECommunityViewModel, navController: NavHostController) {
    val viewModelState by viewModel.mState.collectAsState()
    var missingOperations by remember { mutableStateOf(ECommunityViewModel.NR_OPERATIONS) }

    when (viewModelState.mState) {
        LoadingState.State.SUCCESS -> {
            // view model operation succeeded
            missingOperations--
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            missingOperations--
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

    val isLoading = missingOperations > 0

    if (missingOperations == ECommunityViewModel.NR_OPERATIONS) {
        viewModel.init()
    }

    Scaffold(
        modifier = Modifier
            .gesturesDisabled(isLoading)
            .fillMaxSize(),
        topBar = { ECommunityTopBar() }
    ) {
        if (isLoading) LoadingIndicator()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = dimensionResource(id = R.dimen.activity_vertical_margin))
        ) {
//            ECommunityOffline()
            viewModel.newDistribution?.let {
                ECommunityNewDistribution(
                    newDistribution = it,
                    onAccepted = { portion, flexibility ->
                        // TODO
                    }
                )
            }
//            ECommunityNonCompliance()
            ECommunityPerformance()
            ECommunityDivider()
            ECommunityDistribution(viewModel.currentPortion)
        }
    }
}