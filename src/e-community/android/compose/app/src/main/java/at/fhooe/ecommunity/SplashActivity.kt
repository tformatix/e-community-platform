package at.fhooe.ecommunity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Activity managing the start up process
 * check if user is logged => go directly to MainActivity
 * else show StartUp Screen
 * @see ComponentActivity (compose activity)
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = application as ECommunityApplication

        CoroutineScope(Dispatchers.IO).launch {
            val login = application.cloudRESTRepository.authorize()
            val path = if (login == null) {
                // user not authorized
                StartUpActivity::class.java
            } else {
                // user authorized
                MainActivity::class.java
            }
            val intent = Intent(this@SplashActivity, path)
            this@SplashActivity.startActivity(intent)
        }

        setContent {
            ECommunityTheme {// A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoadingIndicator()
                    Splash()
                }
            }
        }
    }
}

@Composable
@Preview
fun Splash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_e_community_logo),
            contentDescription = stringResource(R.string.image_description_logo),
            contentScale = ContentScale.Crop, // crop the image if it's not a square
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .align(Alignment.Center),
        )
    }
}