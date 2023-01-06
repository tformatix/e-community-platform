package at.fhooe.ecommunity.ui.screen.startup.forgot_password

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.util.Validator

/**
 * lets the user initiate a "forgotten password" email
 * @param _viewModel corresponding view model
 * @param _navController navigation controller
 * @param _email (optional) email address from the nav route (prefill email)
 * @see Composable
 */
@Composable
fun ForgotPasswordScreen(_viewModel: ForgotPasswordViewModel, _navController: NavHostController, _email: String?) {
    val emailState = remember { mutableStateOf(TextFieldValue(_email ?: "")) }
    val viewModelState by _viewModel.mState.collectAsState()
    var isLoading = false // indicator whether the view model state is RUNNING (loading) and the gestures of the underlying layout should be disabled

    when (viewModelState.mState) {
        LoadingState.State.RUNNING -> {
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.SUCCESS -> {
            // view model operation succeeded
            _viewModel.backToIdle() // bring the view model back to the idle state
            Toast.makeText(_viewModel.mApplication, _viewModel.mApplication.getString(R.string.forgot_password_toast_success), Toast.LENGTH_LONG)
                .show() // show success message
            _navController.popBackStack() // remove this screen from the backstack
            _navController.popBackStack() // remove previous login screen from the backstack
            _navController.navigate("${Screen.Login.route}?email=${emailState.value.text}") // go back to login
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            _viewModel.backToIdle() // bring the view model back to the idle state
            viewModelState.mException?.let {
                Toast.makeText(_viewModel.mApplication, _viewModel.mApplication.remoteExceptionRepository.exceptionToString(it), Toast.LENGTH_SHORT)
                    .show() // show error message
            }
        }
        else -> {}
    }

    Box(
        modifier = Modifier
            .gesturesDisabled(isLoading) // freeze layout when loading
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.Center), // center vertically
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // logo as image
            Image(
                painter = painterResource(id = R.drawable.ic_e_community_logo),
                contentDescription = stringResource(R.string.image_description_logo),
                contentScale = ContentScale.Crop, // crop the image if it's not a square
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(40.dp))

            // input email
            TextField(
                label = { Text(text = stringResource(R.string.forgot_password_email)) },
                value = emailState.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { emailState.value = it },
            )
            Spacer(modifier = Modifier.height(20.dp))

            // forgot password button
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    enabled = Validator.validateEmail(emailState.value.text), // only clickable if email is valid
                    onClick = {
                        _viewModel.forgotPassword(emailState.value.text)
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = stringResource(R.string.forgot_password_action))
                }
            }
        }
    }
}