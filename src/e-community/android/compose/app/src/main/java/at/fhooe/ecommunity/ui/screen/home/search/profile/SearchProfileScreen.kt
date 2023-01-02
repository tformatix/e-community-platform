package at.fhooe.ecommunity.ui.screen.home.search.profile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.home.search.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchProfileScreen(_memberId: String, _viewModel: SearchProfileViewModel, _navController: NavHostController) {

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    var isLoading = false

    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading = false
            _viewModel.backToIdle()
        }
        LoadingState.State.RUNNING -> {
            Log.d(TAG, "loading")
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.FAILED -> {
            //
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            TopBarSearchProfile(_navController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                _navController.navigate("${Screen.SharingAddOrUpdContract.route}?memberId=${_memberId}")
            }) {
                Image(
                    painter = painterResource(R.drawable.ic_smart_contract),
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp),
                    contentDescription = "new contract",
                )
            }
        }
    ) {
        SearchProfile()
    }
}

@Composable
fun SearchProfile() {

}


@Composable
fun TopBarSearchProfile(_navController: NavHostController) {
    TopAppBar(
        title = { Text(text = "") },
        navigationIcon = {
            IconButton(onClick = { _navController.navigateUp() }) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }},
        actions = { }
    )
}