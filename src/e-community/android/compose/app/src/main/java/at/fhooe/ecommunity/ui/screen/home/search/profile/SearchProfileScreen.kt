package at.fhooe.ecommunity.ui.screen.home.search.profile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.home.search.*
import at.fhooe.ecommunity.ui.screen.sharing.contract.active.ContractTile
import at.fhooe.ecommunity.ui.screen.sharing.contract.active.ShowEnergyData
import at.fhooe.ecommunity.ui.screen.sharing.contract.active.isMemberProposer
import at.fhooe.ecommunity.ui.screen.sharing.contract.add_upd_contract.DialogBoxLoading
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractUpdateState
import at.fhooe.ecommunity.util.ECommunityFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // profile picture
            Image(
                painter = if (isSystemInDarkTheme()) {
                    painterResource(R.drawable.ic_default_profile_pic_white)
                } else {
                    painterResource(R.drawable.ic_default_profile_pic_black)
                },
                modifier = Modifier
                    .height(100.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(2.dp, colorResource(R.color.blue), RoundedCornerShape(50))
                    .padding(bottom = 5.dp),
                contentDescription = stringResource(R.string.profile_picture),
            )

            Row(
                modifier = Modifier
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                ContractTile(
                    _title = "First Energy-Data Date",
                    _content = "12.08.2022",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                ContractTile(
                    _title = "People in Household",
                    _content = "5",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
            }
        }
    }
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