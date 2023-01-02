package at.fhooe.ecommunity.ui.screen.sharing.contract.add_upd_contract

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.model.LegacyLoadingState
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SharingAddOrUpdContract(_memberId: String, _viewModel: SharingViewModel, _navController: NavHostController) {

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    var isLoading = false

    when(state.mState) {
        LegacyLoadingState.State.SUCCESS -> {
            isLoading = false
            _viewModel.backToIdle()
        }
        LegacyLoadingState.State.RUNNING -> {
            Log.d(TAG, "loading")
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LegacyLoadingState.State.FAILED -> {
            //
        }
        else -> {}
    }

    Scaffold(
        topBar = {
            TopBarAddOrUpdContract(_navController)
        }
    ) {
        EditContractForm()
    }
}

@Composable
fun EditContractForm() {

    val startDate = remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))
    }

    val endDate = remember {
        mutableStateOf(LocalDate.now().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))
    }

    val dialogStartDateState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dialogStartDateState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { date ->
            startDate.value = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
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
            endDate.value = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
        }
    }

    Box {
        Column(modifier = Modifier
            .padding(all = 10.dp)) {


            // start date & time of the energy data
            Column {
                Text(
                    text = "Start & End date of the energy data"
                )

                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colors.primary, // standard blue
                            )
                        ) {
                            append(startDate.value)
                        }
                    },
                    onClick = {
                        dialogStartDateState.show()
                    },
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
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