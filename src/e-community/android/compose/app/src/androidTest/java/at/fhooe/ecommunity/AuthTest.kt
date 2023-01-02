package at.fhooe.ecommunity

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.screen.startup.login.LoginViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AuthTest {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun login() = runTest {
        val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as ECommunityApplication
        val loginViewModel = LoginViewModel(application)

        val results = mutableListOf<LoadingState>()
    }
}