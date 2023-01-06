package at.fhooe.ecommunity.ui.screen.startup.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.util.Validator

/**
 * lets the user create a new account
 * @param _viewModel corresponding view model
 * @param _navController navigation controller
 * @param _email (optional) email address from the nav route (prefill email)
 * @param _password (optional) password address from the nav route (prefill password)
 * @see Composable
 */
@Composable
fun RegisterScreen(_viewModel: RegisterViewModel, _navController: NavHostController, _email: String?, _password: String?) {
    val userNameState = remember { mutableStateOf(TextFieldValue()) }
    val emailState = remember { mutableStateOf(TextFieldValue(_email ?: "")) }

    val passwordState = remember { mutableStateOf(TextFieldValue(_password ?: "")) }
    val passwordVisibleState = remember { mutableStateOf(false) } // should password be visible
    val isValidPassword = Validator.validatePassword(passwordState.value.text) // does password match the guidelines

    val repeatPasswordState = remember { mutableStateOf(TextFieldValue(_password ?: "")) }
    val repeatPasswordVisibleState = remember { mutableStateOf(false) } // should repeat password be visible

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
            Toast.makeText(_viewModel.mApplication, _viewModel.mApplication.getString(R.string.register_toast_success), Toast.LENGTH_LONG)
                .show() // show success message
            _navController.popBackStack() // remove this screen from the backstack
            _navController.navigate("${Screen.Login.route}?email=${emailState.value.text}&password=${passwordState.value.text}") // go to login
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

            // input username
            TextField(
                label = { Text(text = stringResource(R.string.register_username)) },
                value = userNameState.value,
                onValueChange = { userNameState.value = it },
            )
            Spacer(modifier = Modifier.height(20.dp))

            // input email
            TextField(
                label = { Text(text = stringResource(R.string.register_email)) },
                value = emailState.value,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { emailState.value = it },
            )
            Spacer(modifier = Modifier.height(20.dp))

            // input password (with show password icon)
            TextField(
                label = { Text(text = stringResource(R.string.register_password)) },
                value = passwordState.value,
                visualTransformation = if (passwordVisibleState.value) VisualTransformation.None else PasswordVisualTransformation(), // password visible or not
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { passwordState.value = it },
                trailingIcon = {
                    // show password icon
                    val image: ImageVector
                    val description: String

                    if (passwordVisibleState.value) {
                        // password currently visible
                        image = Icons.Filled.Visibility // visible icon
                        description = stringResource(R.string.register_show_password)
                    } else {
                        // password currently invisible
                        image = Icons.Filled.VisibilityOff // invisible icon
                        description = stringResource(R.string.register_hide_password)
                    }

                    IconButton(onClick = { passwordVisibleState.value = !passwordVisibleState.value }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // input repeat password (with show password icon)
            TextField(
                label = { Text(text = stringResource(R.string.register_repeat_password)) },
                value = repeatPasswordState.value,
                visualTransformation = if (repeatPasswordVisibleState.value) VisualTransformation.None else PasswordVisualTransformation(), // password visible or not
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { repeatPasswordState.value = it },
                trailingIcon = {
                    // show password icon
                    val image: ImageVector
                    val description: String

                    if (repeatPasswordVisibleState.value) {
                        // password currently visible
                        image = Icons.Filled.Visibility // visible icon
                        description = stringResource(R.string.register_show_password)
                    } else {
                        // password currently invisible
                        image = Icons.Filled.VisibilityOff // invisible icon
                        description = stringResource(R.string.register_hide_password)
                    }

                    IconButton(onClick = { repeatPasswordVisibleState.value = !repeatPasswordVisibleState.value }) {
                        Icon(imageVector = image, description)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // register button
            Box(
                modifier = Modifier
                    .padding(40.dp, 0.dp, 40.dp, 0.dp)
            ) {
                Button(
                    // only clickable if password is valid, username is not empty, email is valid and passwords are the same
                    enabled = isValidPassword &&
                            Validator.validateNotEmpty(userNameState.value.text) &&
                            Validator.validateEmail(emailState.value.text) &&
                            passwordState.value.text == repeatPasswordState.value.text,
                    onClick = {
                        _viewModel.register(
                            userNameState.value.text,
                            emailState.value.text,
                            passwordState.value.text
                        )
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = stringResource(R.string.register_action))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            // to login button
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colors.primary, // standard blue
                        )
                    ) {
                        append(stringResource(R.string.register_login))
                    }
                },
                onClick = {
                    _navController.popBackStack() // remove this screen from the backstack
                    _navController.navigate("${Screen.Login.route}?email=${emailState.value.text}&password=${passwordState.value.text}") // go to login
                },
                style = TextStyle(
                    fontSize = 14.sp
                )
            )
        }

        if (!isValidPassword) {
            // password invalid -> show password hint with guidelines
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(60.dp),
                text = stringResource(R.string.register_password_guidelines),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error, // red
                style = MaterialTheme.typography.caption // smaller
            )
        }
    }
}