package at.fhooe.ecommunity.ui.screen.e_community

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTopBar
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.monitoring.ECommunityPerformance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNewDistribution
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityNonCompliance
import at.fhooe.ecommunity.ui.screen.e_community.component.notifcation.ECommunityOffline

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ECommunityScreen(viewModel: ECommunityViewModel, navController: NavHostController) {
    val viewModelState by viewModel.mState.collectAsState()
    var isLoading = false // indicator whether the view model state is RUNNING (loading) and the gestures of the underlying layout should be disabled

    when (viewModelState.mState) {
        LoadingState.State.RUNNING -> {
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.SUCCESS -> {
            // view model operation succeeded
            viewModel.backToIdle() // bring the view model back to the idle state
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            viewModel.backToIdle() // bring the view model back to the idle state
        }
        else -> {}
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
            ECommunityOffline()
            ECommunityNewDistribution()
            ECommunityNonCompliance()
            ECommunityPerformance()
            ECommunityDivider()
            ECommunityDistribution()
        }
    }
}