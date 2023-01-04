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
import androidx.compose.material.icons.outlined.MultipleStop
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.sharing.ConsentContract
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import okhttp3.internal.cacheGet
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
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
            TopBarAddOrUpdContract(_navController)
        }
    ) {
        EditContractForm(consentContract)
    }
}

@Composable
fun EditContractForm(_consentContract: MutableState<ConsentContract>) {
    Box {
        Column(modifier = Modifier
            .padding(all = 10.dp)) {

            // time-span energy data
            FormTimeSpan(_consentContract)

            // validity date of contract
            FormValidityOfContract(_consentContract)

            // price per 1h energy data
            FormPricePerEnergyData(_consentContract)

            // summary of the price
            FormSummaryPrice(_consentContract)

            // submit contract
            FormSubmitContract(_consentContract)
        }
    }
}

@Composable
fun FormTimeSpan(_consentContract: MutableState<ConsentContract>) {
    val startDate = remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    }

    val endDate = remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    }

    // init consent contract
    _consentContract.value.startEnergyDataDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    _consentContract.value.endEnergyDataDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val dialogStartDateState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialogStartDateState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            startDate.value = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            _consentContract.value.startEnergyDataDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }

    val dialogEndDateState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialogEndDateState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            endDate.value = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            _consentContract.value.endEnergyDataDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }

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
                        value = startDate.value,
                        onValueChange = {
                            startDate.value = it
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
                        value = endDate.value,
                        onValueChange = {
                            endDate.value = it
                        },
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
}

@Composable
fun FormValidityOfContract(_consentContract: MutableState<ConsentContract>) {
    val startDate = remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
    }

    _consentContract.value.contractValidityDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val dialogStartDateState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialogStartDateState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            startDate.value = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            _consentContract.value.contractValidityDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
            enabled = false,
            value = startDate.value,
            onValueChange = {
                startDate.value = it
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
}

@Composable
fun FormPricePerEnergyData(_consentContract: MutableState<ConsentContract>) {
    val pricePerOneHour = remember {
        mutableStateOf("")
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
                value = pricePerOneHour.value,
                onValueChange = {
                    pricePerOneHour.value = it

                    Log.d(TAG, "before recomp: ${_consentContract.value.startEnergyDataDate}")
                    // recreate a new ConsentContract for re-composition (to show summary)
                    _consentContract.value = ConsentContract(
                        _consentContract.value.startEnergyDataDate,
                        _consentContract.value.endEnergyDataDate,
                        _consentContract.value.contractValidityDate,
                        it,
                        0,
                        ""
                    )
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
}

@Composable
fun FormSummaryPrice(_consentContract: MutableState<ConsentContract>) {

    // consent must be ready to calculate summary
    if (!_consentContract.value.isReadyForSubmit()) {
        return
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)) {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = stringResource(R.string.sharing_contract_summary)
        )

        Log.d(TAG, _consentContract.value.startEnergyDataDate)
        // add hours because date picker uses whole day
        _consentContract.value.startEnergyDataDate += "T00:00:00.000000"
        _consentContract.value.endEnergyDataDate += "T23:59:00.000000"

        val startDateTime = LocalDateTime.parse(_consentContract.value.startEnergyDataDate)
        val endDateTime = LocalDateTime.parse(_consentContract.value.endEnergyDataDate)
        val hours = Duration.between(startDateTime, endDateTime).toHours()

        Column(modifier = Modifier
            .padding(top = 10.dp)
        ) {
            Text(
                text = "${_consentContract.value.pricePerOneHour} ETH x $hours hours"
            )

            Divider(modifier = Modifier
                .padding(top = 5.dp, bottom = 5.dp)
                .height(1.dp))

            Text(
                text = "${_consentContract.value.pricePerOneHour.toFloat() * hours} ETH",
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@Composable
fun FormSubmitContract(_consentContract: MutableState<ConsentContract>) {
    if (!_consentContract.value.isReadyForSubmit()) {
        return
    }

    Button(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp),
        onClick = {
        }) {
        Text(
            text = stringResource(R.string.sharing_contract_submit),
            color = Color.White
        )
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