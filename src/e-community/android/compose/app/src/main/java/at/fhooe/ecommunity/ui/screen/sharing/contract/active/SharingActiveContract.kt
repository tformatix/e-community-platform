package at.fhooe.ecommunity.ui.screen.sharing.contract.active

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.data.local.entity.MeterDataHistContract
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import at.fhooe.ecommunity.ui.screen.sharing.contract.add_upd_contract.DialogBoxLoading
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractUpdateState
import at.fhooe.ecommunity.util.ECommunityFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharingActiveContract(_contractId: String, _viewModel: SharingViewModel, _navController: NavHostController) {

    Log.e(TAG, _contractId)
    val activeContract = remember {
        mutableStateOf(ConsentContract(
            contractId = "",
            state = 0,
            addressContract = null,
            addressProposer = "",
            addressConsenter = "",
            startEnergyData = "",
            endEnergyData = "",
            validityOfContract = "",
            pricePerHour = "",
            totalPrice = ""
        ))
    }

    // collect changes from the tileRepository
    val meterDataHist: List<MeterDataHistContract> by _viewModel.getApplication().meterDataHistContractRepository.getMeterDataHistDataForContract(_contractId).collectAsState(
        initial = emptyList()
    )

    val addressLocal = remember { mutableStateOf("") }

    LaunchedEffect(true) {
        addressLocal.value = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b"
        CoroutineScope(Dispatchers.Default).launch {
            activeContract.value = _viewModel.mApplication.contractRepository.getContract(_contractId)
        }
    }

    Scaffold(
        topBar = { TopBarActiveContract(_navController) }
    ) {
        ShowActiveContract(activeContract, _viewModel, meterDataHist, addressLocal, _navController)
    }
}

@Composable
fun ShowActiveContract(
    _activeContract: MutableState<ConsentContract>,
    _viewModel: SharingViewModel,
    _meterDataHist: List<MeterDataHistContract>,
    _addressLocal: MutableState<String>,
    _navController: NavHostController
) {

    val hasSyncStarted = remember {
        mutableStateOf(false)
    }

    val isSyncingEnergyData = remember {
        mutableStateOf(false)
    }

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

            Row(modifier = Modifier
                .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                val validityDate = if (_activeContract.value.validityOfContract.isNullOrEmpty()) {
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                } else {
                    val date = ECommunityFormatter(LocalContext.current)
                        .convertUnixTimestampToDate(_activeContract.value.validityOfContract.toString())
                    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }

                ContractTile(
                    _title = stringResource(R.string.sharing_contract_validity_of_contract),
                    _content = validityDate,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                ContractTile(
                    _title = stringResource(R.string.sharing_contract_value_of_contract),
                    _content = _activeContract.value.totalPrice.toString(),
                    _iconId = R.drawable.ic_eth,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
            }

            ECommunityDivider()

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.sharing_contract_timespan_energy_data)
            )

            Row(modifier = Modifier
                .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                val startDate = if (_activeContract.value.startEnergyData.isNullOrEmpty()) {
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                } else {
                    val date = ECommunityFormatter(LocalContext.current)
                        .convertUnixTimestampToDate(_activeContract.value.startEnergyData.toString())
                    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }

                val endDate = if (_activeContract.value.endEnergyData.isNullOrEmpty()) {
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                } else {
                    val date = ECommunityFormatter(LocalContext.current)
                        .convertUnixTimestampToDate(_activeContract.value.endEnergyData.toString())
                    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }

                ContractTile(
                    _title = stringResource(R.string.sharing_contract_start_energy_data),
                    _content = startDate,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )

                ContractTile(
                    _title = stringResource(R.string.sharing_contract_end_energy_data),
                    _content = endDate,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
            }

            // check if the history meter data is already synced
            Log.d(TAG, "count Meter Data: ${_meterDataHist.count()}")

            // check if user is proposer or consenter
            if (isMemberProposer(_activeContract.value, _addressLocal.value)) {
                // if no meterDataHist values are found, show sync button
                if (_meterDataHist.isEmpty()) {
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                        onClick = {
                            isSyncingEnergyData.value = true
                            hasSyncStarted.value = true

                            _viewModel.requestHistoryData(isSyncingEnergyData, _activeContract.value)
                        }) {
                        Text(
                            text = stringResource(R.string.sharing_contract_sync_values),
                            color = Color.White
                        )
                    }
                }
                else {
                    Text(
                        text = stringResource(R.string.sharing_contract_energy_data),
                        textAlign = TextAlign.Center
                    )
                    LazyColumn(modifier = Modifier
                        .padding(top = 10.dp)) {
                        items(_meterDataHist) { item ->
                            ShowEnergyData(item)
                        }
                    }
                }

                if (hasSyncStarted.value) {
                    DialogBoxLoading(
                        _isLoading = isSyncingEnergyData,
                        _waitingText = stringResource(R.string.sharing_contract_sync_values),
                        _navigationRoute = null,
                        _navController = _navController
                    )
                }
            }
            else {
                // show revoke consent button
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    onClick = {
                        _viewModel.updateContract(_activeContract.value, ContractUpdateState.CONTRACT_REVOKE.ordinal)
                    }) {
                    Text(
                        text = stringResource(R.string.sharing_contract_revoke_consent),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ShowEnergyData(_meterDataHist: MeterDataHistContract) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        elevation = 10.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                val date = Date(Timestamp.valueOf(_meterDataHist.timeStamp?.replace("T", " ")).time)
                val formatDate = SimpleDateFormat("dd.MM.YYYY")
                val formatTime = SimpleDateFormat("hh:mm")

                Text(
                    text = "${formatDate.format(date)} ${formatTime.format(date)} Uhr"
                )
            }

            Row(modifier = Modifier
                .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                _meterDataHist.activeEnergyPlus?.let {
                    ContractTile(
                        _title = "Consumption",
                        _content = "$it Wh",
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )
                }

                _meterDataHist.activeEnergyMinus?.let {
                    ContractTile(
                        _title = "Feed In",
                        _content = "$it W",
                        _textColor = colorResource(R.color.value_good),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContractTile(_title: String, _textColor: Color = Color.Black, _content: String, _iconId: Int? = null, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)) // round shape
            .background(colorResource(R.color.gray_light))
            .padding(8.dp)
    ) {
        // title
        Text(
            text = _title,
            color = _textColor,
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
        )
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            // content
            Text(
                text = _content,
                color = _textColor,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
            )

            _iconId?.let {
                Image(
                    painter = painterResource(_iconId),
                    modifier = Modifier
                        .padding(bottom = 3.dp)
                        .height(20.dp)
                        .width(20.dp),
                    contentDescription = stringResource(R.string.sharing_wallet_eth),
                )
            }
        }
    }
}

fun isMemberProposer(_contract: ConsentContract, _addressLocal: String): Boolean {
    _contract.addressProposer?.let {
        return it.uppercase() == _addressLocal.uppercase()
    }
    return true
}

@Composable
fun TopBarActiveContract(_navController: NavHostController) {

    TopAppBar(
        title = {
            Text(
                text =  "${stringResource(R.string.sharing_contracts_active)} ${stringResource(R.string.sharing_contract)}"
            )},
        navigationIcon = {
            IconButton(onClick = { _navController.navigateUp() }) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }
        },
        actions = { }
    )
}