package at.fhooe.smartmeter

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import at.fhooe.smartmeter.database.NotificationViewModel
import at.fhooe.smartmeter.database.NotificationViewModelFactory
import at.fhooe.smartmeter.databinding.ActivityMainBinding
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.ui.home.HomeConfiguration
import at.fhooe.smartmeter.ui.startup.StartUpActivity
import at.fhooe.smartmeter.util.*
import kotlin.streams.toList

// logging tag
const val TAG = "SmartMeter"

class MainActivity : AppCompatActivity(), IFragmentMessageService, NavController.OnDestinationChangedListener {

    private var mTag = "MainActivity"
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mEncryptedPreferences: EncryptedPreferences
    private lateinit var mNavController: NavController
    private lateinit var mTutorialManager: TutorialManager
    private lateinit var mToolbar: Toolbar
    private lateinit var mActiveDataModeHome: ActiveDataMode

    private val mNotificationViewModel: NotificationViewModel by viewModels {
        NotificationViewModelFactory((this.application as SmartMeterApplication).notificationRepository)
    }

    /**
     * MainActivity gets created
     * init navController and others
     * @see Bundle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mEncryptedPreferences = EncryptedPreferences(applicationContext)
        mTutorialManager = TutorialManager(applicationContext)

        val navView: BottomNavigationView = mBinding.navView

        mNavController = findNavController(R.id.nav_host_fragment_activity_main)
        mNavController.addOnDestinationChangedListener(this)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_news, R.id.navigation_home, R.id.navigation_e_community, R.id.navigation_profile
            )
        )

        mActiveDataModeHome = ActiveDataMode.REALTIME

        /*
        mToolbar = findViewById<Toolbar>(R.id./activity_main_toolbar)

        mToolbar.findViewById<ImageView>(R.id.tutorial_toolbar_info).setOnClickListener {
            showInfo(mNavController.currentDestination?.id)
        }

        mToolbar.findViewById<ImageView>(R.id.tutorial_toolbar_skip).setOnClickListener {
            showSkipDialog(mNavController.currentDestination?.id)
        }

        // setup actionBar
        setSupportActionBar(mToolbar)*
         */

        //NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration)
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        navView.setupWithNavController(mNavController)


        // if tutorial mode is active, disabled after pages
        // user has to go through or skip each page
        /*if (mEncryptedPreferences.sharedPreferences?.getBoolean(getString(R.string.shared_prefs_tutorial_mode), false) == true) {
            navView.menu[Constants.MAIN_NAV_HOME].isEnabled = false
            navView.menu[Constants.MAIN_NAV_E_COM].isEnabled = false
            navView.menu[Constants.MAIN_NAV_USER].isEnabled = false
        }
        else {
            supportActionBar?.hide()
        }*/
    }

    /**
     * communication between Activity/Fragment
     *
     */
    override fun onCommunicate(code: Int, bundle: Bundle?) {
        when(code) {
            Constants.SHOW_STARTUP_ACTIVITY -> {
                val intent = Intent(this, StartUpActivity::class.java)
                startActivity(intent)
                finish()
            }
            Constants.TUTORIAL_NEWS_FINISHED -> {
                mBinding.navView.menu[1].isEnabled = true

                mNavController.navigate(R.id.navigation_e_community)
            }

            Constants.SHOW_LOCAL_DISCOVERY -> {
                mNavController.navigate(R.id.navigation_local_discovery)
            }

            Constants.SHOW_LOCAL_ADD_NETWORK -> {
                mNavController.navigate(R.id.navigation_local_add_network)
            }

            Constants.SHOW_LOCAL_CONNECT_WIFI -> {
                bundle?.let {
                    mNavController.navigate(R.id.navigation_local_connect_wifi, it)
                }
            }

            Constants.SHOW_LOCAL_SUMMARY -> {
                mNavController.navigate(R.id.navigation_local_summary)
            }

            Constants.SHOW_LOCAL_DEVICE_SETTINGS -> {
                mNavController.navigate(R.id.navigation_local_device_settings)
            }

            Constants.SHOW_HOME -> {
                mNavController.navigate(R.id.navigation_home)
            }

            Constants.ACTIVE_DATA_MODE_HOME_RT -> {
                mActiveDataModeHome = ActiveDataMode.REALTIME
            }

            Constants.ACTIVE_DATA_MODE_HOME_HIST -> {
                mActiveDataModeHome = ActiveDataMode.HISTORY
            }
        }
    }

    /**
     * set up action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * navigate Up button is enabled
     * go back to previous fragment
     */
    override fun onSupportNavigateUp(): Boolean {
        return mNavController.navigateUp()
    }

    /**
     * respond to actions in the tutorial mode
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

            R.id.tutorial_top_menu_info -> {
                showInfo(mNavController.currentDestination?.id)
            }

            R.id.tutorial_top_menu_skip -> {
                showSkipDialog(mNavController.currentDestination?.id)
            }

            R.id.menu_check -> {
                // go to summary fragment when device settings are finished
                if (mNavController.currentDestination?.id == R.id.navigation_local_device_settings) {
                    mNavController.navigate(R.id.navigation_local_summary)
                }
            }

            else -> {
                return false
            }
        }

        return true
    }

    override fun onNavigateUp(): Boolean {
        return super.onNavigateUp()
    }

    /**
     * when destination change:
     *      remove badge (if exists)
     *      set visibility of actionbar items
     */
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        if (destination.id == R.id.navigation_home) {
            val activeDataMode = HomeConfiguration.getActiveDataMode()

            destination.label = if (activeDataMode == ActiveDataMode.REALTIME) {
                getString(R.string.realtime)
            }
            else {
                getString(R.string.history)
            }

        }

        // remove badge
        mBinding.navView.getBadge(destination.id)?.apply {
            isVisible = false
            clearNumber()
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        Log.d(mTag, "item click: ${item.itemId}")
        return super.onContextItemSelected(item)
    }

    /**
     * tells the user info about a tutorial page
     * @return true = stay on the page, false = skip and go to the next page
     */
    private fun showInfo(id: Int?) {
        val infoBuilder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
                .setTitle("Info")
        }

        val textFormatter = TextFormatter(applicationContext)

        when (id) {
            R.id.navigation_news -> {
                Log.d(mTag, "showInfo: news")

                infoBuilder.setMessage(textFormatter.convertHtmlToString(R.string.tutorial_info_news))

                infoBuilder.setPositiveButton(R.string.tutorial_go_home,
                    DialogInterface.OnClickListener { _, _ ->
                        mNavController.navigate(R.id.navigation_home)
                    })

                // after info was shown, guide the user to the next page (home)
                with(mBinding.navView) {
                    menu[1].isEnabled = true
                    getOrCreateBadge(R.id.navigation_home).isVisible = true
                }
            }

            R.id.navigation_home -> {
                Log.d(mTag, "showInfo: home")
            }

            R.id.navigation_e_community -> {
                Log.d(mTag, "showInfo: e_community")
            }

            R.id.navigation_profile -> {
                Log.d(mTag, "showInfo: profile")
            }
        }

        // cancel do nothing
        infoBuilder.setNegativeButton(android.R.string.cancel) { _, _ -> }

        // show dialog
        infoBuilder.create().show()
    }

    /**
     * user can skip this page or the whole tutorial
     */
    private fun showSkipDialog(id: Int?) {
        val skipBuilder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
                .setTitle("Skip")
        }

        val textFormatter = TextFormatter(applicationContext)

        when (id) {
            R.id.navigation_news -> {
                Log.d(mTag, "showInfo: news")
            }

            R.id.navigation_home -> {
                Log.d(mTag, "showInfo: home")
            }

            R.id.navigation_e_community -> {
                Log.d(mTag, "showInfo: e_community")
            }

            R.id.navigation_profile -> {
                Log.d(mTag, "showInfo: profile")
            }
        }

        skipBuilder.apply {
            setMessage("Not recommended but you can skip this page.. and continue with the Home page")

            // skip page
            setPositiveButton("Skip Page",
                DialogInterface.OnClickListener { dialog, id ->
                    Log.d(mTag, "skip News")
                    mNavController.navigate(R.id.navigation_home)
                })

            // cancel the skip process
            setNegativeButton(android.R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->
                })
        }

        // cancel do nothing
        skipBuilder.setNegativeButton(android.R.string.cancel) { _, _ -> }

        // show dialog
        skipBuilder.create().show()
    }
}