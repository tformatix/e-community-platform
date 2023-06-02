package at.fhooe.ecommunity.ui.screen.sharing.contract.details

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.Constants.CONTRACT_ACTIVE
import at.fhooe.ecommunity.Constants.CONTRACT_CLOSED
import at.fhooe.ecommunity.Constants.CONTRACT_PENDING
import at.fhooe.ecommunity.Constants.CONTRACT_REQUESTS
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractDataUsage
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractState
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractUpdateState
import at.fhooe.ecommunity.util.EncryptedPreferences
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import java.util.*
import kotlin.streams.toList

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharingContractDetailsList(_startState: Int, _viewModel: SharingViewModel, _navController: NavHostController) {

    // state of selected contract state
    val selectedItem = remember{mutableStateOf( "")}

    selectedItem.value = when(_startState) {
        ContractState.CONTRACT_ACTIVE.ordinal -> {
            stringResource(R.string.sharing_contracts_active)
        }

        ContractState.CONTRACT_REQUESTS_FOR_ME.ordinal -> {
            stringResource(R.string.sharing_contracts_requests)
        }

        ContractState.CONTRACT_PENDING.ordinal -> {
            stringResource(R.string.sharing_contracts_pending)
        }

        ContractState.CONTRACT_CLOSED.ordinal -> {
            stringResource(R.string.sharing_contracts_closed)
        }
        else -> {
            stringResource(R.string.sharing_contracts_active)
        }
    }

    // get address of me
    val addressLocal = remember { mutableStateOf("") }
    val sharedPrefs = EncryptedPreferences(_viewModel.getApplication())
    val memberId = sharedPrefs.getCredentials()?.memberId.toString()

    // collect changes from the contractRepository
    val contracts: List<ConsentContract> by _viewModel.getApplication().contractRepository.getConsentContracts().collectAsState(initial = emptyList())

    LaunchedEffect(true) {
        //_viewModel.getAddressForMember(memberId, addressLocal)
        addressLocal.value = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b"
    }

    Scaffold(
        topBar = {
            //TopBarContractDetailsList(_navController)
        }
    ) {
        ContractDetailsList(
            selectedItem,
            _viewModel,
            contracts,
            addressLocal,
            _navController
        )
    }
}

@Composable
fun ContractDetailsList(
    selectedItem: MutableState<String>,
    _viewModel: SharingViewModel,
    _contracts: List<ConsentContract>,
    _addressLocal: MutableState<String>,
    _navController: NavHostController
) {

    val contractStates = mapOf(
        stringResource(R.string.sharing_contracts_active) to CONTRACT_ACTIVE,
        stringResource(R.string.sharing_contracts_requests) to CONTRACT_REQUESTS,
        stringResource(R.string.sharing_contracts_pending) to CONTRACT_PENDING,
        stringResource(R.string.sharing_contracts_closed) to CONTRACT_CLOSED
    )

    Column() {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)) {
            items(items = contractStates.keys.toList(), itemContent = { state ->
                StateSelection(selectedItem, state)
            })
        }

        Divider(thickness = 1.dp)

        ShowContracts(_viewModel, _contracts, _addressLocal, contractStates[selectedItem.value], _navController)
    }
}

@Composable
fun StateSelection(_selectedState: MutableState<String>, _state: String) {
    val borderStroke = if (isSystemInDarkTheme()) {
        (-1).dp
    } else { 2.dp }

    Column(modifier = Modifier
        .selectable(
            selected = _selectedState.value == _state,
            onClick = { _selectedState.value = _state }
        )
        .clip(RoundedCornerShape(10.dp))
        .border(BorderStroke(borderStroke, Color.Gray))
        .background(
            if (_selectedState.value == _state) {
                colorResource(id = R.color.blue)
            } else {
                if (isSystemInDarkTheme()) {
                    Color.DarkGray
                } else {
                    Color.White
                }
            }
        )
        .padding(all = 5.dp)
        ) {

        if (_selectedState.value == _state) {
            Text(modifier = Modifier,
                text = _state,
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }
        else {
            Text(modifier = Modifier,
                text = _state,
                style = MaterialTheme.typography.h6,
                color = if (isSystemInDarkTheme()) {
                    Color.White
                } else {
                    Color.Black
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ShowContracts(
    _viewModel: SharingViewModel,
    _contracts: List<ConsentContract>,
    _addressLocal: MutableState<String>,
    _selectedState: Int?,
    _navController: NavHostController
) {
    val state = when(_selectedState) {
        CONTRACT_ACTIVE -> { ContractState.CONTRACT_ACTIVE.ordinal }
        CONTRACT_PENDING -> { ContractState.CONTRACT_PENDING.ordinal }
        else -> { ContractState.CONTRACT_CLOSED.ordinal }
    }

    val filteredContracts = remember {
        mutableStateListOf<ConsentContract>()
    }

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    if (_selectedState == CONTRACT_REQUESTS) {
        filteredContracts.clear()
        filteredContracts.addAll(_contracts.stream().filter { x -> (x.addressProposer?.uppercase() == _addressLocal.value.uppercase())
                &&
                x.state != ContractState.CONTRACT_CLOSED.ordinal
        }.toList())

        val tabRowItems = listOf(
            stringResource(R.string.sharing_contract_requests_proposer),
            stringResource(R.string.sharing_contract_requests_consenter)
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                    color = MaterialTheme.colors.secondary
                )
            },
        ) {
            tabRowItems.forEachIndexed { index, item ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        // change filter based on selected tab
                        filteredContracts.clear()

                        if (index == 0 /* PROPOSER */) {
                            filteredContracts.addAll(_contracts.stream().filter { x -> (x.addressProposer?.uppercase() == _addressLocal.value.uppercase()
                                    && x.addressConsenter?.uppercase() != _addressLocal.value.uppercase())
                                    &&
                                    x.state != ContractState.CONTRACT_CLOSED.ordinal
                            }.toList())
                        } else /* CONSENTER */ {
                            filteredContracts.addAll(_contracts.stream().filter { x -> (x.addressConsenter?.uppercase() == _addressLocal.value.uppercase()
                                    && x.addressProposer?.uppercase() != _addressLocal.value.uppercase())
                                    &&
                                    x.state != ContractState.CONTRACT_CLOSED.ordinal
                            }.toList())
                        }

                        Log.d(TAG, "filter: ${filteredContracts.count()}")

                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        } },
                    text = {
                        Text(
                            text = item
                        )
                    }
                )
            }
        }
        HorizontalPager(
            count = tabRowItems.size,
            state = pagerState,
        ) {
            tabRowItems[pagerState.currentPage]
        }
    }
    else {
        filteredContracts.clear()
        filteredContracts.addAll(_contracts.stream().filter { x -> x.state == state
        }.toList())
    }

    ShowContractsList(filteredContracts, _viewModel, _selectedState, _navController)
}

@Composable
fun ShowContractsList(
    _filteredContracts: List<ConsentContract>,
    _viewModel: SharingViewModel,
    _selectedState: Int?,
    _navController: NavHostController
) {
    Log.d(TAG, "count: ${_filteredContracts.count()}")
    LazyColumn(modifier = Modifier
        .padding(all = 10.dp)) {
        items(_filteredContracts) { contract ->
            Log.d(TAG, "show: ${contract.totalPrice}")
            ContractRow(contract, _viewModel, _selectedState, _navController)
        }
    }
}

@Composable
fun ContractRow(
    _contract: ConsentContract,
    _viewModel: SharingViewModel,
    _selectedState: Int?,
    _navController: NavHostController
) {
    val swipeAccept = SwipeAction(
        icon = painterResource(R.drawable.ic_accept),
        background = Color.Green,
        isUndo = true,
        onSwipe = {
            _viewModel.updateContract(_contract, ContractUpdateState.CONTRACT_ACCEPT.ordinal)
            _navController.navigate(Screen.Sharing.route)
        }
    )

    val swipeReject = SwipeAction(
        icon = painterResource(R.drawable.ic_close),
        background = Color.Red,
        isUndo = true,
        onSwipe = {
            _viewModel.updateContract(_contract, ContractUpdateState.CONTRACT_REJECT.ordinal)
            _navController.navigate(Screen.Sharing.route)
        }
    )

    val isAcceptNeeded = _selectedState == CONTRACT_REQUESTS
    val isRejectNeeded = _selectedState != CONTRACT_CLOSED

    SwipeableActionsBox(modifier = Modifier
        .padding(all = 5.dp)
        .clip(RoundedCornerShape(20.dp)),
        startActions = if (isAcceptNeeded) { listOf(swipeAccept) }
                        else { emptyList() },
        endActions = if (isRejectNeeded) { listOf(swipeReject) }
        else { emptyList() }) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onContractSelected(_contract, _selectedState, _navController)
            }
            .background(
                if (isSystemInDarkTheme()) {
                    Color.DarkGray
                } else {
                    Color.White
                }
            )
        ) {
            // image
            Image(
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(50))
                    .padding(top = 5.dp),
                painter = if (isSystemInDarkTheme()) {
                        painterResource(R.drawable.ic_default_profile_pic_white)
                    } else {
                    painterResource(R.drawable.ic_default_profile_pic_black)
                           },
                contentDescription = "profile_image"
                )

            val list = listOf("tformatix")

                Column(modifier = Modifier
                    .padding(all = 5.dp)) {
                    Text(
                        text = list.asSequence().shuffled().find { true }.toString(),
                        style = MaterialTheme.typography.h6
                    )

                    // choose usage of the energy data
                    val dataUsageTypes = mapOf(
                        ContractDataUsage.FORECASTS.ordinal to stringResource(R.string.sharing_contract_data_usage_forecasts),
                        ContractDataUsage.STATISTICAL.ordinal to stringResource(R.string.sharing_contract_data_usage_statistical),
                        ContractDataUsage.PERFORMANCE.ordinal to stringResource(R.string.sharing_contract_data_usage_performance),
                        ContractDataUsage.CANDIDATE_E_COMMUNITY.ordinal to stringResource(R.string.sharing_contract_data_usage_e_community)
                    )

                    Column() {
                        _contract.dataUsage?.let {
                            Text(
                                text = dataUsageTypes.getValue(it)
                            )
                        }

                        Row(modifier = Modifier
                            .padding(top = 5.dp)) {
                            _contract.totalPrice?.let {
                                Text(
                                    text = it,
                                    color = colorResource(R.color.value_good)
                                )
                            }
                            Image(
                                painter = painterResource(R.drawable.ic_eth),
                                modifier = Modifier
                                    .padding(bottom = 5.dp)
                                    .height(25.dp)
                                    .width(25.dp),
                                contentDescription = stringResource(R.string.sharing_wallet_eth),
                            )
                        }
                    }
                }

                // contract state
                var state = when (_contract.state) {
                    ContractState.CONTRACT_PAYMENT_PENDING.ordinal -> {
                        stringResource(R.string.sharing_contract_state_payment_pending)
                    }
                    ContractState.CONTRACT_ACTIVE_WITHDRAW_READY.ordinal -> {
                        stringResource(R.string.sharing_contract_state_withdraw_ready)
                    }
                    else -> {
                        ""
                    }
                }
            state = ""

                Text(
                    text = state
                )
            }
        }
}

fun onContractSelected(
    _contract: ConsentContract,
    _selectedItem: Int?,
    _navController: NavHostController
) {
    if (_selectedItem == CONTRACT_ACTIVE) {
        // go to active contract view
        _navController.navigate(Screen.SharingActiveContract.route +
            "?contractId=${_contract.contractId}"
        )
    }
    else {
        _navController.navigate(
            Screen.SharingAddOrUpdContract.route +
                "?contractId=${_contract.contractId}" +
                "&contractState=${_selectedItem}"
        )
    }
}

@Composable
fun TopBarContractDetailsList(_navController: NavHostController) {

    TopAppBar(
            title = { Text(text = stringResource(R.string.sharing_contract_view_contracts)) },
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