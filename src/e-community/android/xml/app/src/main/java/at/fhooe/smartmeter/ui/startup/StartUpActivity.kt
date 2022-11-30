package at.fhooe.smartmeter.ui.startup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.commit
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.MainActivity
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.databinding.ActivityStartUpBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.ui.login.LoginFragment
import at.fhooe.smartmeter.ui.login.PasswordForgottenFragment
import at.fhooe.smartmeter.ui.registration.RegisterFragment
import at.fhooe.smartmeter.ui.registration.ConfirmationFragment
import at.fhooe.smartmeter.util.EncryptedPreferences
import at.fhooe.smartmeter.util.TutorialManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.apis.AuthApi
import org.openapitools.client.models.LoginMemberModel

class StartUpActivity : AppCompatActivity(), IFragmentMessageService {

    private lateinit var mBinding: ActivityStartUpBinding
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mTutorialManager: TutorialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStartUpBinding.inflate(layoutInflater)

        // todo michi 10.01.2022: we can use the new splashscreen api for startup screen
        //val splashScreen = installSplashScreen()

        setContentView(mBinding.root)
        mEncryptedPreferences = EncryptedPreferences(applicationContext)
        mTutorialManager = TutorialManager(applicationContext)

        // do not show action bar in startup
        supportActionBar?.hide()

        var refreshToken = mEncryptedPreferences.sharedPreferences?.getString(
            getString(R.string.shared_prefs_refresh_token), null
        )

        if (refreshToken != null) {
            var memberId = "";
            var accessToken = "";
            onCommunicate(Constants.SHOW_LOADING_SCREEN, null)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val authApi = AuthApi("https://e-community.azurewebsites.net")
                    val loginAnswer = authApi.authRefreshPost(refreshToken)
                    refreshToken = loginAnswer.refreshToken.toString()
                    accessToken = loginAnswer.accessToken.toString()
                    memberId = loginAnswer.memberId.toString()

                    val editor = mEncryptedPreferences.sharedPreferences?.edit()
                    editor?.apply {
                        putString(getString(R.string.shared_prefs_member_id), memberId)
                        putString(getString(R.string.shared_prefs_access_token), accessToken)
                        putString(getString(R.string.shared_prefs_refresh_token), refreshToken)
                    }?.apply()

                    onCommunicate(Constants.SHOW_MAIN_ACTIVITY, null)
                } catch (e: Exception) {
                    Log.e("MIMIS DEBUGTAG ERROR", e.toString())
                    onCommunicate(Constants.SHOW_MAIN_ACTIVITY, null)
                }
            }
        }
    }

    /**
     * communication between Activity/Fragment
     */
    override fun onCommunicate(code: Int, bundle: Bundle?) {
        when (code) {
            Constants.SHOW_REGISTER -> {
                supportFragmentManager.commit {
                    replace(mBinding.activityStartUpFragment.id, RegisterFragment())
                    addToBackStack("registerFragment")
                }
            }
            Constants.SHOW_MAIN_ACTIVITY -> {
                theme.applyStyle(R.style.Theme_SmartMeter_Tutorial, true)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            Constants.SHOW_PASSWORD_FORGOTTEN -> {
                supportFragmentManager.commit {
                    replace(mBinding.activityStartUpFragment.id, PasswordForgottenFragment())
                    addToBackStack("passwordForgottenFragment")
                }
            }
            Constants.SHOW_LOGIN -> {
                mTutorialManager.clear()
                supportFragmentManager.commit {
                    replace(mBinding.activityStartUpFragment.id, LoginFragment())
                    addToBackStack("loginFragment")
                }
                /*val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()*/
            }
            Constants.SHOW_REGISTER_CONFIRMATION -> {
                supportFragmentManager.commit {
                    replace(mBinding.activityStartUpFragment.id, ConfirmationFragment())
                    addToBackStack("registerConfirmationFragment")
                }
            }
            Constants.SHOW_LOADING_SCREEN -> {
                supportFragmentManager.commit {
                    replace(mBinding.activityStartUpFragment.id, LoadingScreen())
                    addToBackStack("registerConfirmationFragment")
                }
            }
        }
    }
}