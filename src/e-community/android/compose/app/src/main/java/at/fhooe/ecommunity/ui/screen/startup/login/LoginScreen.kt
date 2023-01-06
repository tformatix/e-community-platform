package at.fhooe.ecommunity.ui.screen.startup.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.MainActivity
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.util.Validator

/**
 * lets the user login to the account
 * @param _viewModel corresponding view model
 * @param _navController navigation controller
 * @param _email (optional) email address from the nav route (prefill email)
 * @param _password (optional) password address from the nav route (prefill password)
 * @see Composable
 */
@Composable
fun LoginScreen(_viewModel: LoginViewModel, _navController: NavHostController, _email: String?, _password: String?) {
    val emailState = remember { mutableStateOf(TextFieldValue(_email ?: "")) }

    val passwordState = remember { mutableStateOf(TextFieldValue(_password ?: "")) }
    val passwordVisibleState = remember { mutableStateOf(false) }  // should password be visible

    val viewModelState by _viewModel.mState.collectAsState()
    var isLoading = false // indicator whether the view model state is RUNNING (loading) and the gestures of the underlying layout should be disabled
    var isResendConfirmationEmailVisible = false // should the resend confirmation email button be visible

    when (viewModelState.mState) {
        LoadingState.State.RUNNING -> {
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.SUCCESS -> {
            // view model operation succeeded
            when (viewModelState.mId) {
                LoginViewModel.LoadingStateId.LOGIN.ordinal -> {
                    // state from the "login" action
                    _viewModel.backToIdle() // bring the view model back to the idle state
                    LocalContext.current.run {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        this.startActivity(intent) // go to main activity
                    }
                }
                LoginViewModel.LoadingStateId.RESEND_CONFIRMATION_EMAIL.ordinal -> {
                    // state from the "resend confirmation email" action
                    _viewModel.backToIdle() // bring the view model back to the idle state
                    Toast.makeText(
                        _viewModel.mApplication,
                        _viewModel.mApplication.getString(R.string.login_toast_resend_confirmation_email_success),
                        Toast.LENGTH_SHORT
                    )
                        .show() // show success message
                }
            }
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            _viewModel.backToIdle() // bring the view model back to the idle state
            viewModelState.mException?.let {
                val remoteException = _viewModel.mApplication.remoteExceptionRepository.exceptionToRemoteException(it)
                if (remoteException.mType == RemoteException.Type.EMAIL_NOT_CONFIRMED) {
                    // error is due to email is not confirmed -> show resend confirmation email button
                    isResendConfirmationEmailVisible = true
                } else {
                    // other error
                    Toast.makeText(
                        _viewModel.mApplication,
                        _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(remoteException),
                        Toast.LENGTH_SHORT
                    ).show() // show error message
                }
            }
        }
        else -> {}
    }

    Box(
        modifier = Modifier
            .gesturesDisabled(isLoading)
            .fillMaxSize()
    ) {
        if (isResendConfirmationEmailVisible) {
            // resend confirmation email button
            ClickableText(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(20.dp),
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.error, // red
                        )
                    ) {
                        append(stringResource(R.string.login_resend_confirmation_email))
                    }
                },
                onClick = {
                    _viewModel.resendConfirmationEmail(emailState.value.text, passwordState.value.text)
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            )
        }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.Center),
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
                label = { Text(text = stringResource(R.string.login_email)) },
                value = emailState.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { emailState.value = it },
            )
            Spacer(modifier = Modifier.height(20.dp))

            // input password (with show password icon)
            TextField(
                label = { Text(text = stringResource(R.string.login_password)) },
                value = passwordState.value,
                visualTransformation =
                if (passwordVisibleState.value) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { passwordState.value = it },
                trailingIcon = {
                    // show password icon
                    val image: ImageVector
                    val description: String

                    if (passwordVisibleState.value) {
                        // password currently visible
                        image = Icons.Filled.Visibility
                        description = stringResource(R.string.login_show_password)
                    } else {
                        // password currently invisible
                        image = Icons.Filled.VisibilityOff
                        description = stringResource(R.string.login_hide_password)
                    }

                    IconButton(onClick = { passwordVisibleState.value = !passwordVisibleState.value }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // login button
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    // only clickable if email is valid and password is not empty
                    enabled = Validator.validateEmail(emailState.value.text) &&
                            Validator.validateNotEmpty(passwordState.value.text),
                    onClick = {
                        _viewModel.login(emailState.value.text, passwordState.value.text)
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = stringResource(R.string.login_action))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // to password forgotten button
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.primary, // standard blue
                        )
                    ) {
                        append(stringResource(R.string.login_forgot_password))
                    }
                },
                onClick = {
                    _navController.navigate("${Screen.ForgotPassword.route}?email=${emailState.value.text}") // go to forgot password
                },
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }

        // to register button
        ClickableText(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary, // standard blue
                    )
                ) {
                    append(stringResource(R.string.login_sign_up))
                }
            },
            onClick = {
                _navController.popBackStack() // remove this screen from the backstack
                _navController.navigate("${Screen.Register.route}?email=${emailState.value.text}&password=${passwordState.value.text}") // go to register
            },
            style = TextStyle(
                fontSize = 14.sp,
            )
        )
    }
}