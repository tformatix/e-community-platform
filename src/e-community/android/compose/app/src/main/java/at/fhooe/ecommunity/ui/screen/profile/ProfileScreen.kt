package at.fhooe.ecommunity.ui.screen.profile

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.StartUpActivity
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalMemberDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalSmartMeterDto
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator

/**
 * Screen for Profile (display user settings & add new SmartMeters)
 * @param _viewModel viewModel for ProfileScreen
 * @param _navController navController for navigation to Pairing
 * @see Composable
 */
@Composable
fun ProfileScreen(_viewModel: ProfileViewModel, _navController: NavHostController) {

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    // remember state of the member
    var isLoading = false
    val member = remember { mutableStateOf(MinimalMemberDto()) }
    val smartMeters = _viewModel.mSmartMeters.collectAsState()

    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading = false
            //Profile(isLoading, member, smartMeters, _navController)
            _viewModel.backToIdle()
            Log.d(TAG, "success")
        }
        LoadingState.State.RUNNING -> {
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.FAILED -> {
            //
        }
        else -> {}
    }

    // build screen (TopBar and GridLayout)
    Scaffold(
        topBar = { TopBarProfile(_viewModel) }
    ) {
        Profile(isLoading, member, smartMeters, _navController)
    }

    if (member.value.userName == null || smartMeters.value.isEmpty()) {
        _viewModel.loadProfile(member)
    }

    // load member and smart meter info

    /*LaunchedEffect(true) {
        /*_viewModel.getMinimalMemberInfo(member)
        _viewModel.getMinimalSmartMeters()*/
        _viewModel.loadProfile(member)
    }*/
}

/**
 * layout of the profile page
 * @param _isLoading check if page is loading
 * @param _member current member
 * @param _smartMeters list of smartMeters of user
 * @param _navController navigate to pairing
 * @see Composable
 */
@Composable
fun Profile(
    _isLoading: Boolean,
    _member: MutableState<MinimalMemberDto>,
    _smartMeters: State<List<MinimalSmartMeterDto>>,
    _navController: NavHostController
) {
    Log.d(TAG, "show profile ${_smartMeters.value.toString()}")

    Box(
        modifier = Modifier
            .gesturesDisabled(_isLoading)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // profile picture
            Image(
                painter = painterResource(R.drawable.ic_profile_picture),
                modifier = Modifier
                    .height(100.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(50)),
                contentDescription = stringResource(R.string.profile_picture),
            )

            Spacer(modifier = Modifier.height(40.dp))


            // check if username is loaded
            var username = _member.value.userName ?: run {
                "username"
            }

            // username
            TextField(
                value = username,
                onValueChange = { username = it},
                textStyle = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
            )

            Spacer(modifier = Modifier.height(40.dp))

            // list of settings
            Column {
                // smart meters
                Row(modifier = Modifier
                    .fillMaxWidth()) {

                    // smart meters title
                    Text(
                        text = LocalContext.current.getString(R.string.profile_smart_meters),
                        fontWeight = FontWeight.Bold
                    )

                    // add new smart meter
                    val addSmartMeterText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colors.primary,
                            )
                        ) {
                            append(stringResource(R.string.profile_add_smart_meter))
                        }
                    }

                    ClickableText(
                        modifier = Modifier
                            .padding(start = 20.dp),
                        text = addSmartMeterText,
                        onClick = {
                            _navController.navigate(Screen.PairingDiscovery.route)
                        }
                    )
                }

                // list of current smart meters
                LazyColumn {
                    items(_smartMeters.value) { item ->
                        SmartMeterItem(item)
                    }
                }
            }
        }
    }
}

/**
 * display each smart meter item
 * @param _item Smart Meter item (minimal)
 * @see Composable
 */
@Composable
fun SmartMeterItem(_item: MinimalSmartMeterDto?) {
    _item?.let {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // name
            it.name?.let {
                Text(
                    text = it,
                    fontSize = 16.sp
                )
            }

            // description
            it.description?.let {
                Text(
                    text = it,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * topBar for logout
 * @param _viewModel viewModel for ProfileScreen
 * @see Composable
 */
@Composable
fun TopBarProfile(_viewModel: ProfileViewModel) {

    val context = LocalContext.current

    TopAppBar(
        title = { Text(text = LocalContext.current.getString(R.string.profile_title_top_bar)) },
        actions = {

            // logout user
            IconButton(onClick = { logout(context, _viewModel) }) {
                Icon(painterResource(R.drawable.ic_logout), LocalContext.current.getString(R.string.profile_logout))
            }
        }
    )
}/**
 * logout user, delete refresh token and go to startup activity
 * @param _context context for starting StartUpActivity
 * @param _viewModel viewModel for ProfileScreen
 */
fun logout(_context: Context, _viewModel: ProfileViewModel) {
    _viewModel.logout()

    // StartUp Activity
    _context.run {
        val intent = Intent(this, StartUpActivity::class.java)
        this.startActivity(intent)
    }
}