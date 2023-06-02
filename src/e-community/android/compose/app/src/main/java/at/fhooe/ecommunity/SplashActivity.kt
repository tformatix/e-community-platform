package at.fhooe.ecommunity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.ui.screen.startup.forgot_password.ForgotPasswordScreen
import at.fhooe.ecommunity.ui.screen.startup.forgot_password.ForgotPasswordViewModel
import at.fhooe.ecommunity.ui.screen.startup.login.LoginScreen
import at.fhooe.ecommunity.ui.screen.startup.login.LoginViewModel
import at.fhooe.ecommunity.ui.screen.startup.register.RegisterScreen
import at.fhooe.ecommunity.ui.screen.startup.register.RegisterViewModel
import at.fhooe.ecommunity.ui.theme.ECommunityTheme
import com.google.accompanist.pager.*
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Activity managing the start up process
 * check if user is logged in => go directly to MainActivity
 * else show StartUp Screen
 * @see ComponentActivity (compose activity)
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as ECommunityApplication

        tryLogin(application)

        setContent {
            ECommunityTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        LoadingIndicator(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_e_community_logo),
                            contentDescription = stringResource(R.string.image_description_logo),
                            contentScale = ContentScale.Crop, // crop the image if it's not a square
                            modifier = Modifier
                                .padding(bottom = 15.dp)
                                .size(160.dp)
                                .clip(CircleShape)
                                .align(Alignment.Center),
                        )
                    }
                }
            }
        }
    }

    private fun tryLogin(_application: ECommunityApplication) {
        val handler = CoroutineExceptionHandler { _, _exc ->
            // auth error --> Startup Activity
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    _application,
                    _application.remoteExceptionRepository.exceptionToString(_exc),
                    Toast.LENGTH_SHORT
                ).show()
            }
            goToActivity(StartUpActivity::class.java)
        }

        CoroutineScope(Dispatchers.IO + handler).launch {
            val login = _application.cloudRESTRepository.authorize(true)
            goToActivity(
                if (login == null) {
                    // user not authorized
                    StartUpActivity::class.java
                } else {
                    // user authorized
                    MainActivity::class.java
                }
            )
        }
    }

    /**
     * go to specific activity
     * @param _path Activity Path
     */
    private fun goToActivity(_path: Class<out ComponentActivity>) {
        val intent = Intent(this@SplashActivity, _path)
        this@SplashActivity.startActivity(intent)
    }
}