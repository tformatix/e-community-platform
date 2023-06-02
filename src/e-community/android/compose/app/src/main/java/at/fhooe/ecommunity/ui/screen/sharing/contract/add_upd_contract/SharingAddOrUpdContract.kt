package at.fhooe.ecommunity.ui.screen.sharing.contract.add_upd_contract

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.Constants.CONTRACT_CLOSED
import at.fhooe.ecommunity.Constants.CONTRACT_NEW
import at.fhooe.ecommunity.Constants.CONTRACT_PENDING
import at.fhooe.ecommunity.Constants.CONTRACT_REQUESTS
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.local.entity.BlockchainBalance
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ConsentContractModel
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.DropDown
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractDataResolution
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractDataUsage
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractState
import at.fhooe.ecommunity.ui.screen.sharing.models.ContractUpdateState
import at.fhooe.ecommunity.util.ECommunityFormatter
import at.fhooe.ecommunity.util.EncryptedPreferences
import at.fhooe.ecommunity.util.TimeSpan
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharingAddOrUpdContract(
    _memberId: String,
    _contractId: String,
    _contractState: Int,
    _viewModel: SharingViewModel,
    _navController: NavHostController
) {

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    // saved as state to know when signalr sent the data
    val isLoading = remember { mutableStateOf(false) }

    // get address of me
    val addressLocal = remember { mutableStateOf("") }

    // get address of consenter
    val addressConsenter = remember { mutableStateOf("") }

    // get address of proposer
    val addressProposer = remember { mutableStateOf("") }

    // get memberId of proposer
    val sharedPrefs = EncryptedPreferences(_viewModel.getApplication())
    val proposerId = sharedPrefs.getCredentials()?.memberId.toString()

    // save state of blockchain account balance
    val blockchainBalance: BlockchainBalance by _viewModel.getApplication().blockchainBalanceRepository.getBalance().collectAsState(initial = BlockchainBalance(received = "0", sent = "0", balance = "0"))

    // contract from db (PENDING, CLOSED)
    val storedContract = remember {
        mutableStateOf(
            ConsentContract(
                contractId = "",
                state = 0,
                addressContract = null,
                addressProposer = null,
                addressConsenter = null,
                startEnergyData = null,
                endEnergyData = null,
                validityOfContract = null,
                pricePerHour = null,
                totalPrice = null,
                dataUsage = null,
                timeResolution = null
            )
        )
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
        addressLocal.value = "0xEA3576B983ab5270D60bF0FFF167aDC86075690b"
        when(_contractState) {
            CONTRACT_NEW -> {
                isLoading.value = true
                _viewModel.requestBlockchainAccountBalance(isLoading)

            }
            CONTRACT_REQUESTS -> {
                isLoading.value = true
                _viewModel.requestBlockchainAccountBalance(isLoading)
                //_viewModel.getAddressForMember(_memberId, addressConsenter)
                //_viewModel.getAddressForMember(proposerId, addressProposer)

                CoroutineScope(Dispatchers.Default).launch {
                    storedContract.value = _viewModel.mApplication.contractRepository.getContract(_contractId)
                    isLoading.value = false
                }
            }
            CONTRACT_PENDING, CONTRACT_CLOSED -> {
                isLoading.value = true

                CoroutineScope(Dispatchers.Default).launch {
                    storedContract.value = _viewModel.mApplication.contractRepository.getContract(_contractId)
                    isLoading.value = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopBarAddOrUpdContract(_contractState, _navController)
        }
    ) {
        EditContractForm(
            isLoading,
            _viewModel,
            blockchainBalance,
            _navController,
            addressConsenter,
            addressProposer,
            storedContract,
            _contractState,
            addressLocal
        )
    }
}

@Composable
fun EditContractForm(
    _isLoading: MutableState<Boolean>,
    _viewModel: SharingViewModel,
    _blockchainAccountBalance: BlockchainBalance?,
    _navController: NavHostController,
    _addressConsenter: MutableState<String>,
    _addressProposer: MutableState<String>,
    _storedContract: MutableState<ConsentContract>,
    _contractState: Int,
    _addressLocal: MutableState<String>
) {
    if (_isLoading.value) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }

    val isFormEditAllowed = _contractState != CONTRACT_NEW

    Box(modifier = Modifier
        .verticalScroll(rememberScrollState())) {
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

            val hasDepositedToContract = remember {
                mutableStateOf(false)
            }

            val isDepositingToContract = remember {
                mutableStateOf(false)
            }

            val hasWithdrawnFromContract = remember {
                mutableStateOf(false)
            }

            val isWithdrawingFromContract = remember {
                mutableStateOf(false)
            }

            val selectedDataUsage = remember {
                mutableStateOf(0)
            }

            val selectedTimeResolution = remember {
                mutableStateOf(0)
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
            Column(modifier = Modifier
                .fillMaxWidth()
                .gesturesDisabled(isFormEditAllowed)) {
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

            ECommunityDivider()

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
                .gesturesDisabled(isFormEditAllowed)) {
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
                    enabled = false,
                    onValueChange = {
                        validityDate.displayDate = it
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = stringResource(R.string.e_community_offline_icon_desc),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable { dialogValidityDateState.show() }
                        )
                    }
                )
            }

            ECommunityDivider()

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp)
                .gesturesDisabled(isFormEditAllowed)) {
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

            ECommunityDivider()

            // choose usage of the energy data
            val dataUsageTypes = mapOf(
                stringResource(R.string.sharing_contract_data_usage_forecasts) to ContractDataUsage.FORECASTS,
                stringResource(R.string.sharing_contract_data_usage_statistical) to ContractDataUsage.STATISTICAL,
                stringResource(R.string.sharing_contract_data_usage_performance) to ContractDataUsage.PERFORMANCE,
                stringResource(R.string.sharing_contract_data_usage_e_community) to ContractDataUsage.CANDIDATE_E_COMMUNITY
            )

            _storedContract.value.dataUsage?.let {
                selectedDataUsage.value = it
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .gesturesDisabled(isFormEditAllowed)) {

                Text(modifier = Modifier
                    .weight(1f),
                    text = stringResource(R.string.sharing_contract_data_usage)
                )

                Spacer(Modifier.weight(0.8f))

                DropDown(
                    items = dataUsageTypes.keys.toList(),
                    onSelected = { _, item ->
                        dataUsageTypes[item]?.let {  }
                    },
                    fontSize = 12.sp,
                    modifier = Modifier
                        .weight(1f),
                    preSelected = selectedDataUsage.value
                )
            }

            // choose resolution of the energy data
            val dataResolutionTypes = mapOf(
                stringResource(R.string.sharing_contract_data_resolution_15min) to ContractDataResolution.TIME_15MIN,
                stringResource(R.string.sharing_contract_data_resolution_30min) to ContractDataResolution.TIME_30MIN,
                stringResource(R.string.sharing_contract_data_resolution_1h) to ContractDataResolution.TIME_1H,
                stringResource(R.string.sharing_contract_data_resolution_12h) to ContractDataResolution.TIME_12H,
                stringResource(R.string.sharing_contract_data_resolution_1d) to ContractDataResolution.TIME_1D
            )

            _storedContract.value.timeResolution?.let {
                selectedTimeResolution.value = it
            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .gesturesDisabled(isFormEditAllowed)) {

                Text(modifier = Modifier
                    .weight(1f),
                    text = stringResource(R.string.sharing_contract_data_resolution)
                )

                Spacer(Modifier.weight(0.8f))

                DropDown(
                    items = dataResolutionTypes.keys.toList(),
                    onSelected = { _, item ->
                        dataResolutionTypes[item]?.let {  }
                    },
                    fontSize = 12.sp,
                    modifier = Modifier
                        .weight(1f),
                    preSelected = selectedTimeResolution.value
                )
            }

            ECommunityDivider()

            var totalPrice = 0f

            // summary of the price
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .gesturesDisabled(isFormEditAllowed)) {
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

                        _blockchainAccountBalance?.balance?.let {
                            val rounded = BigDecimal(it.toDouble()).setScale(4, RoundingMode.HALF_UP)

                            Text(modifier = Modifier.padding(start = 5.dp),
                                text = "$rounded ETH",
                                color = Color.Green
                            )
                        }

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

            val formatter = ECommunityFormatter(LocalContext.current)

            // fill in Form when contract is viewed
            if (_contractState != CONTRACT_NEW) {

                // energy start date
                _storedContract.value.startEnergyData?.let {
                    val date = formatter.convertUnixTimestampToDate(it)

                    startDate = startDate.copy(
                        displayDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        shortDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    )
                }

                // energy end date
                _storedContract.value.endEnergyData?.let {
                    val date = formatter.convertUnixTimestampToDate(it)

                    endDate = endDate.copy(
                        displayDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        shortDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    )
                }

                // energy end date
                _storedContract.value.validityOfContract?.let {
                    val date = formatter.convertUnixTimestampToDate(it)

                    validityDate = validityDate.copy(
                        displayDate = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                        shortDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    )
                }

                // price per Hour
                _storedContract.value.pricePerHour?.let {
                    pricePerOneHour = it
                }
            }

            when (_contractState) {
                CONTRACT_NEW -> {
                    // submit contract
                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                        enabled = pricePerOneHour.isNotEmpty() && pricePerOneHour.toFloat() > 0f
                                && _blockchainAccountBalance?.balance != null && totalPrice <= _blockchainAccountBalance.balance!!.toFloat(),
                        onClick = {
                            consentContractModel.value = ConsentContractModel(
                                contractId = null,
                                state = 0,
                                addressContract = null,
                                //addressProposer = _addressProposer.value,
                                //addressConsenter = _addressConsenter.value,
                                addressProposer = "0x89a41a3ae94f64b8dc3683787ef2363e9d0bc957", //boot0
                                addressConsenter = "0xea3576b983ab5270d60bf0fff167adc86075690b", //michi rasp
                                startEnergyData = formatter.convertDateToUnixTimestamp(startDate.shortDate + "T00:00:00.000000").toString(),
                                endEnergyData = formatter.convertDateToUnixTimestamp(endDate.shortDate + "T23:59:00.000000").toString(),
                                validityOfContract = formatter.convertDateToUnixTimestamp(validityDate.shortDate + "T23:59:00.000000").toString(),
                                pricePerHour = pricePerOneHour,
                                totalPrice = totalPrice.toString()
                            )

                            hasSubmittedContract.value = true
                            isCreatingConsentContract.value = true

                            Log.d(TAG, "create contract: ${consentContractModel.value}")

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

                            DialogBoxLoading(isCreatingConsentContract, stringResource(R.string.sharing_contract_creating), Screen.Sharing.route, _navController)
                        }
                    }
                }

                CONTRACT_REQUESTS -> {
                    // contract waits for deposit
                    when (_storedContract.value.state) {
                        ContractState.CONTRACT_PAYMENT_PENDING.ordinal -> {
                            // only proposer can deposit
                            if (_storedContract.value.addressProposer == _addressLocal.value) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                    enabled = pricePerOneHour.isNotEmpty() && pricePerOneHour.toFloat() > 0f
                                            && _blockchainAccountBalance?.balance != null && totalPrice <= _blockchainAccountBalance.balance!!.toFloat(),
                                    onClick = {
                                        hasDepositedToContract.value = true
                                        isDepositingToContract.value = true

                                        _viewModel.depositToContract(isDepositingToContract, _storedContract.value)
                                    }) {
                                    Text(
                                        text = stringResource(R.string.sharing_contract_deposit),
                                        color = Color.White
                                    )
                                }
                            }
                            else {
                                Text(
                                    text = stringResource(R.string.sharing_contract_only_proposer_deposit)
                                )
                            }
                        }

                        ContractState.CONTRACT_ACTIVE_WITHDRAW_READY.ordinal -> {
                            if (_storedContract.value.addressConsenter == _addressLocal.value) {
                                Button(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                    onClick = {
                                        hasWithdrawnFromContract.value = true
                                        isWithdrawingFromContract.value = true

                                        _viewModel.withdrawFromContract(isWithdrawingFromContract, _storedContract.value)
                                    }) {
                                    Text(
                                        text = stringResource(R.string.sharing_contract_withdraw_from_contract),
                                        color = Color.White
                                    )
                                }
                            }
                            else {
                                Text(
                                    text = stringResource(R.string.sharing_contract_only_consenter_withdraw)
                                )
                            }
                        }
                    }

                    if (hasDepositedToContract.value) {
                        DialogBoxLoading(isDepositingToContract, stringResource(R.string.sharing_contract_deposit), null, _navController)
                    }
                    if (hasWithdrawnFromContract.value) {
                        DialogBoxLoading(isWithdrawingFromContract, stringResource(R.string.sharing_contract_withdraw_from_contract), null, _navController)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogBoxLoading(
    _isLoading: MutableState<Boolean>,
    _waitingText: String,
    _navigationRoute: String?,
    _navController: NavHostController
) {

    val cornerRadius: Dp = 16.dp
    val paddingStart: Dp = 56.dp
    val paddingEnd: Dp = 56.dp
    val paddingTop: Dp = 32.dp
    val paddingBottom: Dp = 32.dp
    val progressIndicatorColor: Color = colorResource(R.color.blue)
    val progressIndicatorSize: Dp = 80.dp

    var showDialog by remember {mutableStateOf(true)}

    if (showDialog) {
        Dialog(
            onDismissRequest = { }
        ) {
            Surface(
                elevation = 4.dp,
                shape = RoundedCornerShape(cornerRadius)
            ) {
                Column(modifier = Modifier
                    .padding(start = paddingStart, end = paddingEnd, top = paddingTop),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    if (_isLoading.value) {
                        ProgressIndicatorLoading(
                            progressIndicatorSize = progressIndicatorSize,
                            progressIndicatorColor = progressIndicatorColor
                        )
                    }
                    else {
                        Icon(modifier = Modifier
                            .size(progressIndicatorSize),
                            imageVector = Icons.Outlined.Check,
                            contentDescription = _waitingText,
                            tint = colorResource(id = R.color.value_good)
                        )
                    }

                    // Gap between progress indicator and text
                    Spacer(modifier = Modifier.height(32.dp))

                    if (_isLoading.value) {
                        // Please wait text
                        Text(modifier = Modifier
                            .padding(bottom = paddingBottom),
                            text = _waitingText,
                            color = Color.White,
                            fontSize = 20.sp
                        )

                        // Please wait text
                        Text(modifier = Modifier
                            .padding(bottom = paddingBottom),
                            text = stringResource(R.string.sharing_contract_waiting),
                            fontSize = 16.sp
                        )
                    }
                    else {
                        Text(modifier = Modifier
                            .padding(bottom = paddingBottom),
                            text = stringResource(R.string.sharing_contract_creating_finished),
                            fontSize = 20.sp
                        )

                        // go sharing dashboard
                        ClickableText(modifier = Modifier
                            .padding(bottom = paddingBottom),
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colors.primary, // standard blue
                                    )
                                ) {
                                    append(stringResource(R.string.sharing_contract_ok))
                                }
                            },
                            onClick = {
                                showDialog = false
                                _navigationRoute?.let { _navController.navigate(it) }
                            },
                            style = TextStyle(
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ProgressIndicatorLoading(progressIndicatorSize: Dp, progressIndicatorColor: Color) {

    val infiniteTransition = rememberInfiniteTransition()

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 600
            }
        )
    )

    CircularProgressIndicator(
        progress = 1f,
        modifier = Modifier
            .size(progressIndicatorSize)
            .rotate(angle)
            .border(
                12.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        Color.White, // add background color first
                        progressIndicatorColor.copy(alpha = 0.1f),
                        progressIndicatorColor
                    )
                ),
                shape = CircleShape
            ),
        strokeWidth = 1.dp,
        color = Color.White // Set background color
    )
}


@Composable
fun TopBarAddOrUpdContract(_contractState: Int, _navController: NavHostController) {

    var title = when(_contractState.toInt()) {
        CONTRACT_REQUESTS -> { stringResource(R.string.sharing_contracts_requested) }
        CONTRACT_PENDING -> { stringResource(R.string.sharing_contracts_pending) }
        CONTRACT_CLOSED -> { stringResource(R.string.sharing_contracts_closed) }
        else -> { stringResource(R.string.sharing_contract_action_new_contract) }
    }

    title += " ${stringResource(R.string.sharing_contract)}"

    TopAppBar(
        title = { Text(text =  title) },
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

