package at.fhooe.ecommunity.ui.screen.sharing.contract.add_upd_contract

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MultipleStop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.BlockchainAccountBalanceDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ConsentContractModel
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.ui.screen.sharing.ConsentContract
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import at.fhooe.ecommunity.util.TimeSpan
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharingAddOrUpdContract(_memberId: String, _viewModel: SharingViewModel, _navController: NavHostController) {

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    val consentContract = remember {
        mutableStateOf(
            ConsentContract("", "", "", "", 0, "")
        )
    }

    // saved as state to know when signalr sent the data
    val isLoading = remember {
        mutableStateOf(false)
    }

    // save state of blockchain account balance
    val blockchainAccountBalance = remember {
        mutableStateOf(
            BlockchainAccountBalanceDto(
                "0", "0", "0"
            ))
    }


    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading.value = false
            _viewModel.backToIdle()
        }
        LoadingState.State.RUNNING -> {
            isLoading.value = true
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            _viewModel.backToIdle() // bring the view model back to the idle state
            Log.d(TAG, "error")

            if (state.mException == null) {
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(
                        RemoteException(RemoteException.Type.NO_INTERNET)
                    ),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
            state.mException?.let {
                val remoteException = _viewModel.mApplication.remoteExceptionRepository.exceptionToRemoteException(it)
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(
                        RemoteException(RemoteException.Type.NO_INTERNET)
                    ),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
        }
        else -> {}
    }

    LaunchedEffect(true) {
        isLoading.value = true
        _viewModel.requestBlockchainAccountBalance(isLoading, blockchainAccountBalance)
    }

    Scaffold(
        topBar = {
            TopBarAddOrUpdContract(_navController)
        }
    ) {
        EditContractForm(isLoading, _viewModel, blockchainAccountBalance)
    }
}

@Composable
fun EditContractForm(
    _isLoading: MutableState<Boolean>,
    _viewModel: SharingViewModel,
    _blockchainAccountBalance: MutableState<BlockchainAccountBalanceDto>
) {
    if (_isLoading.value) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }

    Box {
        Column(modifier = Modifier
            .padding(all = 10.dp)) {

            // time-span energy data
            var startDate by remember {
                mutableStateOf(TimeSpan(
                    LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                )
            }

            var endDate by remember {
                mutableStateOf(TimeSpan(
                    LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                )
            }

            // validity date of contract
            var validityDate by remember {
                mutableStateOf(TimeSpan(
                    LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                )
            }

            val isCreatingConsentContract = remember {
                mutableStateOf(false)
            }

            val hasSubmittedContract = remember {
                mutableStateOf(false)
            }

            val consentContractModel = remember {
                mutableStateOf(ConsentContractModel(
                    contractId = null,
                    state = 0,
                    addressContract = null,
                    addressProposer = null,
                    addressConsenter = null,
                    startEnergyData = "",
                    endEnergyData = "",
                    validityOfContract = "",
                    pricePerHour = "",
                    totalPrice = ""
                ))
            }

            // price per 1h energy data
            var pricePerOneHour by remember {
                mutableStateOf("0")
            }

            val dialogValidityDateState = rememberMaterialDialogState()
            val dialogStartDateState = rememberMaterialDialogState()
            val dialogEndDateState = rememberMaterialDialogState()

            val shape = RoundedCornerShape(10.dp)

            // start date & time of the energy data
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(CenterHorizontally),
                    text = stringResource(R.string.sharing_contract_timespan_energy_data)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row {
                        Column(horizontalAlignment = CenterHorizontally,
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth()) {

                            TextField(
                                modifier = Modifier
                                    .clip(shape),
                                enabled = false,
                                value = startDate.displayDate,
                                onValueChange = { startDate.displayDate = it },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = stringResource(R.string.e_community_offline_icon_desc),
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .clickable { dialogStartDateState.show() }
                                    )
                                }
                            )
                        }

                        Icon(
                            imageVector = Icons.Outlined.MultipleStop,
                            contentDescription = "between",
                            modifier = Modifier
                                .padding(top = 17.dp)
                                .weight(0.30f)
                        )

                        Column(horizontalAlignment = CenterHorizontally,
                            modifier = Modifier
                                .weight(1.0f)
                                .fillMaxWidth()) {

                            TextField(
                                modifier = Modifier
                                    .clip(shape),
                                enabled = false,
                                value = endDate.displayDate,
                                onValueChange = { endDate.displayDate = it },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = "calendar",
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .clickable { dialogEndDateState.show() }
                                    )
                                }
                            )
                        }
                    }
                }
            }

            MaterialDialog(
                dialogState = dialogStartDateState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                datepicker { date ->
                    startDate = startDate.copy(
                        displayDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        shortDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    )
                }
            }

            MaterialDialog(
                dialogState = dialogEndDateState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                datepicker { date ->
                    endDate = endDate.copy(
                        displayDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        shortDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    )
                }
            }

            MaterialDialog(
                dialogState = dialogValidityDateState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                datepicker { date ->
                    validityDate = validityDate.copy(
                        displayDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        shortDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    )

                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)) {
                Text(
                    modifier = Modifier.align(CenterHorizontally),
                    text = stringResource(R.string.sharing_contract_validity_of_contract)
                )

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .padding(top = 10.dp),
                    value = validityDate.displayDate,
                    onValueChange = {
                        validityDate.displayDate = it
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = stringResource(R.string.e_community_offline_icon_desc),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable { dialogStartDateState.show() }
                        )
                    }
                )
            }



            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)) {
                Text(
                    modifier = Modifier.align(CenterHorizontally),
                    text = stringResource(R.string.sharing_contract_price_per_1h)
                )

                Row {
                    TextField(
                        modifier = Modifier
                            .weight(0.8f)
                            .clip(RoundedCornerShape(10.dp))
                            .padding(top = 10.dp),
                        value = pricePerOneHour,
                        onValueChange = {
                            pricePerOneHour = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Text(
                        modifier = Modifier
                            .weight(0.2f)
                            .align(CenterVertically)
                            .padding(start = 10.dp),
                        text = stringResource(R.string.sharing_contract_currency_ethereum_short),
                        style = MaterialTheme.typography.h5
                    )
                }
            }

            var totalPrice = 0f

            // summary of the price
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)) {
                Text(
                    modifier = Modifier.align(CenterHorizontally),
                    text = stringResource(R.string.sharing_contract_summary)
                )

                val startDateTime = LocalDateTime.parse(startDate.shortDate + "T00:00:00.000000")
                val endDateTime = LocalDateTime.parse(endDate.shortDate + "T23:59:00.000000")
                val hours = Duration.between(startDateTime, endDateTime).toHours()

                Column(modifier = Modifier
                    .padding(top = 10.dp)
                ) {
                    Row() {
                        Text(
                            text = stringResource(R.string.sharing_contract_available_balance)
                        )
                        Text(modifier = Modifier.padding(start = 5.dp),
                            text = "${_blockchainAccountBalance.value.balance?.trim()} ETH",
                            color = Color.Green
                        )
                    }

                    Text(modifier = Modifier.padding(top = 5.dp),
                        text = "$pricePerOneHour ETH x $hours hours"
                    )

                    Divider(modifier = Modifier
                        .padding(top = 5.dp, bottom = 5.dp)
                        .height(1.dp))

                    totalPrice = if (pricePerOneHour.isNotEmpty()) {
                        pricePerOneHour.toFloat() * hours
                    } else { 0f }

                    Text(
                        text = "$totalPrice ETH",
                        style = MaterialTheme.typography.h6
                    )
                }
            }

            // submit contract
            Button(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
                enabled = pricePerOneHour.isNotEmpty() && pricePerOneHour.toFloat() > 0f
                        && _blockchainAccountBalance.value.balance != null && totalPrice <= _blockchainAccountBalance.value.balance!!.toFloat(),
                onClick = {
                    consentContractModel.value = ConsentContractModel(
                        contractId = null,
                        state = 0,
                        addressContract = null,
                        addressProposer = null,
                        addressConsenter = "0x6240311ad7a58913f8d3f373a230948f753db9d3",
                        startEnergyData = convertDateToUnixTimestamp(startDate.shortDate + "T00:00:00.000000").toString(),
                        endEnergyData = convertDateToUnixTimestamp(endDate.shortDate + "T23:59:00.000000").toString(),
                        validityOfContract = convertDateToUnixTimestamp(validityDate.shortDate + "T23:59:00.000000").toString(),
                        pricePerHour = pricePerOneHour,
                        totalPrice = totalPrice.toString()
                    )

                    hasSubmittedContract.value = true
                    isCreatingConsentContract.value = true

                    _viewModel.createConsentContract(isCreatingConsentContract, consentContractModel)
                }) {
                Text(
                    text = stringResource(R.string.sharing_contract_submit),
                    color = Color.White
                )
            }

            if (hasSubmittedContract.value) {
                Column(modifier = Modifier
                    .fillMaxWidth()) {

                    Row() {
                        Text(
                            text = stringResource(R.string.sharing_contract_creating)
                        )

                        if (isCreatingConsentContract.value) {
                            CircularProgressIndicator()
                        }
                        else {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.sharing_contract_creating),
                                tint = colorResource(id = R.color.value_good),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopBarAddOrUpdContract(_navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.blue))
            .padding(16.dp, 8.dp, 12.dp, 8.dp),
    ) {
        Row {
            // go back
            IconButton(onClick = { _navController.navigateUp() }) {
                Icon(
                    painterResource(R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }

            var contractName by remember { mutableStateOf(TextFieldValue("")) }

            TextField(
                value = contractName,
                label = { Text(text = stringResource(R.string.sharing_contract_name)) },
                onValueChange = {
                    contractName = it
                },
                colors = TextFieldDefaults.textFieldColors(unfocusedLabelColor = Color.White,
                    focusedLabelColor = Color.White, disabledLabelColor = Color.Gray, errorLabelColor = Color.Red
                )
            )
        }
    }
}

fun convertDateToUnixTimestamp(_date: String): Long {
    val date = LocalDateTime.parse(_date)
    val zoneId: ZoneId = ZoneId.systemDefault()
    return date.atZone(zoneId).toEpochSecond()
}