package at.fhooe.ecommunity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.screen.startup.forgot_password.ForgotPasswordScreen
import at.fhooe.ecommunity.ui.screen.startup.forgot_password.ForgotPasswordViewModel
import at.fhooe.ecommunity.ui.screen.startup.login.LoginScreen
import at.fhooe.ecommunity.ui.screen.startup.login.LoginViewModel
import at.fhooe.ecommunity.ui.screen.startup.register.RegisterScreen
import at.fhooe.ecommunity.ui.screen.startup.register.RegisterViewModel
import at.fhooe.ecommunity.ui.theme.ECommunityTheme
import com.google.accompanist.pager.*
import com.skydoves.landscapist.glide.GlideImage

/**
 * Activity for the start-up (login, register, forgot password, ...)
 * @see ComponentActivity (compose activity)
 */
class StartUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as ECommunityApplication

        setContent {
            ECommunityTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    // declare the navigation graph
                    NavHost(navController = navController, startDestination = Screen.StartUp.route) {
                        composable(Screen.StartUp.route) {
                            MainScreenStartUp(navController)
                        }
                        Screen.Login.arguments?.let { arguments ->
                            composable(
                                Screen.Login.getRouteWithArguments(), arguments = arguments
                            ) { backStack ->
                                LoginScreen(
                                    LoginViewModel(application),
                                    navController,
                                    backStack.arguments?.getString(arguments[0].name),
                                    backStack.arguments?.getString(arguments[1].name),
                                )
                            }
                        }
                        Screen.Register.arguments?.let { arguments ->
                            composable(
                                Screen.Register.getRouteWithArguments(), arguments = arguments
                            ) { backStack ->
                                RegisterScreen(
                                    RegisterViewModel(application),
                                    navController,
                                    backStack.arguments?.getString(arguments[0].name),
                                    backStack.arguments?.getString(arguments[1].name),
                                )
                            }
                        }
                        Screen.ForgotPassword.arguments?.let { arguments ->
                            composable(
                                Screen.ForgotPassword.getRouteWithArguments(), arguments = arguments
                            ) { backStack ->
                                ForgotPasswordScreen(
                                    ForgotPasswordViewModel(application),
                                    navController,
                                    backStack.arguments?.getString(arguments[0].name),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * MainScreen for StartUp
 * show a image pager, login & register
 * @see ExperimentalPagerApi (pager is still experimental)
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreenStartUp(_navController: NavHostController) {

    // remember state of the pager (which page is the current)
    val pagerState = rememberPagerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // image slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .background(Color.White)
            ) {
                PagerContent(_pagerState = pagerState)
            }

            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // image slider indicator
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = Color.Green,
                    inactiveColor = Color.Gray
                )

                // text based on selected image
                ImageText(pagerState.currentPage)
            }
        }

        // tutorial, login, register
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 10.dp, bottom = 20.dp, end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // tutorial screen (not used yet)
            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                Text(
                    text = stringResource(R.string.startup_welcome_learn_more),
                    color = Color(0xFFFF9800) /* Color.Orange */
                )
            }

            // register button
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { _navController.navigate(Screen.Register.route) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.secondary
                )
            ) {
                Text(text = stringResource(R.string.startup_register))
            }

            // login clickable text
            val loginText = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colors.primary,
                    )
                ) {
                    append(stringResource(R.string.startup_login))
                }
            }

            ClickableText(
                text = loginText,
                onClick = { _navController.navigate(Screen.Login.route) }
            )
        }
    }
}

/**
 * Horizontal image slider
 * @see ExperimentalPagerApi (pager is still experimental)
 * @see ExperimentalFoundationApi (pager is still experimental)
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@Composable
fun PagerContent(_pagerState: PagerState) {

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            count = 3,
            state = _pagerState
        ) { pager ->

            when (pager) {
                0 -> {
                    ViewSlider(Constants.URL_IMAGE_1)
                }
                1 -> {
                    ViewSlider(Constants.URL_IMAGE_2)
                }
                2 -> {
                    ViewSlider(Constants.URL_IMAGE_3)
                }
            }
        }
    }
}

/**
 * load image into slider
 * @param _imageUrl url of the gifs
 */
@Composable
fun ViewSlider(_imageUrl: String) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center
    ) {
        // use Glide for image loading
        GlideImage(
            imageModel = _imageUrl,
            contentScale = ContentScale.Inside
        )
    }
}

/**
 * text for each slider image
 * @param _index index of the pager
 */
@Composable
fun ImageText(_index: Int) {
    val text = stringResource(
        id =
        when (_index) {
            0 /* welcome earth */ -> {
                R.string.startup_welcome_earth
            }
            1 /* save money */ -> {
                R.string.startup_welcome_save_money
            }
            else /* 2# social */ -> {
                R.string.startup_welcome_social
            }
        }
    )

    // text from the image
    Text(
        modifier = Modifier.padding(top = 10.dp),
        text = text,
        color = Color.Black
    )
}