package at.fhooe.ecommunity

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.screen.e_community.ECommunityScreen
import at.fhooe.ecommunity.ui.screen.e_community.ECommunityViewModel
import at.fhooe.ecommunity.ui.screen.home.HomeScreen
import at.fhooe.ecommunity.ui.screen.home.HomeViewModel
import at.fhooe.ecommunity.ui.screen.home.search.SearchFilterScreen
import at.fhooe.ecommunity.ui.screen.home.search.SearchScreen
import at.fhooe.ecommunity.ui.screen.home.search.SearchViewModel
import at.fhooe.ecommunity.ui.screen.home.search.profile.SearchProfileScreen
import at.fhooe.ecommunity.ui.screen.home.search.profile.SearchProfileViewModel
import at.fhooe.ecommunity.ui.screen.profile.ProfileScreen
import at.fhooe.ecommunity.ui.screen.profile.ProfileViewModel
import at.fhooe.ecommunity.ui.screen.profile.pairing.*
import at.fhooe.ecommunity.ui.screen.sharing.SharingScreen
import at.fhooe.ecommunity.ui.screen.sharing.SharingViewModel
import at.fhooe.ecommunity.ui.screen.sharing.contract.active.SharingActiveContract
import at.fhooe.ecommunity.ui.screen.sharing.contract.add_upd_contract.SharingAddOrUpdContract
import at.fhooe.ecommunity.ui.screen.sharing.contract.details.SharingContractDetailsList
import at.fhooe.ecommunity.ui.theme.ECommunityTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


/**
 * Activity for Main user experience
 * @see ComponentActivity (compose activity)
 */
class MainActivity : ComponentActivity() {

    private lateinit var mApplication: ECommunityApplication
    private lateinit var mMessageReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mApplication = application as ECommunityApplication

        askNotificationPermission()
        updateFCMToken()

        // broadcast receiver (receives messages from FCMService)
        mMessageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // message received
                Toast.makeText(this@MainActivity, intent?.getStringExtra(Constants.BROADCAST_RECEIVER_NOTIFICATION_EXTRA_MESSAGE), Toast.LENGTH_LONG)
                    .show()
                if (intent?.getStringExtra(Constants.BROADCAST_RECEIVER_NOTIFICATION_EXTRA_ID) == Constants.NOTIFICATION_ID_E_COMMUNITY) {
                    // message for eCommunity screen
                    val eCommunityViewModel = ECommunityViewModel.getInstance(mApplication)
                    if (eCommunityViewModel.isListenerRegistered()) {
                        // user is on eCommunity screen --> reload
                        eCommunityViewModel.initLoad()
                    }
                }

            }
        }

        setContent {
            ECommunityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    /**
     * updates firebase cloud messaging token
     */
    private fun updateFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            mApplication.cloudRESTRepository.updateFCMToken(task.result)
        })
    }

    /**
     * since Android 13: notification permission request
     */
    private fun askNotificationPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, getString(R.string.notification_denied), Toast.LENGTH_LONG).show()
            }
        }

        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(getString(R.string.notification_rationale))
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
                builder.setNegativeButton(android.R.string.cancel) { _, _ -> }
                builder.create().show()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                mMessageReceiver,
                IntentFilter(Constants.BROADCAST_RECEIVER_NOTIFICATION)
            )
    }
}

/**
 * preview for MainScreen()
 * @see Composable
 */
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ECommunityTheme {
        MainScreen()
    }
}

/**
 * Main Composable function (bottom navigation for different screens)
 * @see Composable
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // bottom navigation items
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.ECommunity,
        Screen.Sharing,
        Screen.Profile
    )

    val selectedScreen = remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController, bottomNavItems)
        }
    )
    { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainScreenNavigationConfigurations(navController, selectedScreen)
        }
    }
}

/**
 * navigation graph
 * @param navController navigation controller
 * @see Composable
 */
@Composable
private fun MainScreenNavigationConfigurations(
    navController: NavHostController,
    selectedScreen: MutableState<String>,
) {
    // start with eCommunity screen
    val startDestination = Screen.ECommunity.route
    val application = ((LocalContext.current as MainActivity).application as ECommunityApplication)

    NavHost(navController, startDestination = startDestination) {
        /* Bottom Nav */
        composable(Screen.Home.route) {
            HomeScreen(HomeViewModel(application), navController)
        }
        composable(Screen.ECommunity.route) {
            ECommunityScreen(ECommunityViewModel.getInstance(application))
        }
        composable(Screen.Sharing.route) {
            SharingScreen(SharingViewModel(application), navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(ProfileViewModel(application), navController)
        }
        /* Pairing */
        composable(Screen.PairingDiscovery.route) {
            PairingDiscovery(PairingViewModel(application), navController)
        }
        composable(Screen.PairingSummary.route) {
            PairingSummary(PairingViewModel(application), navController)
        }
        composable(Screen.PairingAddNetwork.route) {
            PairingAddNetwork(PairingViewModel(application), navController)
        }
        composable(Screen.PairingWifiConnect.route) {
            PairingWifiConnect(PairingViewModel(application), navController)
        }
        /* Home - Search */
        composable(Screen.Search.route) {
            SearchScreen(SearchViewModel(application), navController)
        }
        composable(Screen.SearchFilter.route) {
            SearchFilterScreen(SearchViewModel(application), navController)
        }

        Screen.SearchProfile.arguments?.let { arguments ->
            composable(
                Screen.SearchProfile.getRouteWithArguments(), arguments = arguments
            ) { backStack ->
                backStack.arguments?.getString(arguments[0].name)?.let {
                    SearchProfileScreen(
                        it,
                        SearchProfileViewModel(application),
                        navController
                    )
                }
            }
        }

        Screen.SharingAddOrUpdContract.arguments?.let { arguments ->
            composable(Screen.SharingAddOrUpdContract.getRouteWithArguments(), arguments = arguments) { backStack ->
                val memberId = backStack.arguments?.getString(arguments[0].name)
                val contractId = backStack.arguments?.getString(arguments[1].name)
                val contractState = backStack.arguments?.getString(arguments[2].name)

                if (memberId != null && contractId != null && contractState != null) {
                    SharingAddOrUpdContract(
                        memberId,
                        contractId,
                        contractState.toInt(),
                        SharingViewModel(application),
                        navController
                    )
                }
            }
        }

        Screen.SharingContractDetailsList.arguments?.let { arguments ->
            composable(Screen.SharingContractDetailsList.getRouteWithArguments(), arguments = arguments) { backStack ->
                backStack.arguments?.getString(arguments[0].name)?.let {
                    SharingContractDetailsList(
                        it.toInt(),
                        SharingViewModel(application),
                        navController
                    )
                }
            }
        }

        Screen.SharingActiveContract.arguments?.let { arguments ->
            composable(Screen.SharingActiveContract.getRouteWithArguments(), arguments = arguments) { backStack ->
                backStack.arguments?.getString(arguments[0].name)?.let {
                    SharingActiveContract(
                        it,
                        SharingViewModel(application),
                        navController
                    )
                }
            }
        }
    }
}

/**
 * bottom navigation design
 * @param navController navigation controller
 * @param bottomNavItems items of bottom nav
 * @see Composable
 */
@Composable
private fun AppBottomNavigation(navController: NavHostController, bottomNavItems: List<Screen>) {

    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    screen.icon?.let { painterResource(id = it) }?.let {
                        Icon(
                            painter = it,
                            contentDescription = "Icons",
                        )
                    }
                },
                label = { Text(screen.name) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        //launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        //restoreState = true
                    }
                }
            )
        }
    }

    /*BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                selectedContentColor = Teal200,
                alwaysShowLabel = true,
                icon = {
                    screen.icon?.let { painterResource(id = it) }?.let {
                        Icon(
                            painter = it,
                            contentDescription = "Icons",
                            tint = Color.White
                        )
                    }
                },
                selected = currentDestination == screen.route,
                onClick = {
                    if (currentDestination != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                        }
                    }
                })
        }
    }*/
    /*BottomNavigation {
        //val currentRoute = currentRoute(navController)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Log.d(TAG, currentRoute.toString())
        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    screen.icon?.let { painterResource(id = it) }?.let {
                        Icon(
                            painter = it,
                            contentDescription = "Icons",
                            tint = Color.White
                        )
                    }
                },
                label = { Text(screen.name) },
                selected = currentRoute == screen.route,
                alwaysShowLabel = false,
                onClick = {
                    Log.d(TAG, "currentRoute: ${currentRoute} screen.route: ${screen.route}")
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }*/
}

/**
 * current route (used for backStack)
 * @param navController navigation controller
 * @see Composable
 */
@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Log.d(TAG, "key_route ${navBackStackEntry?.arguments?.getString("KEY_ROUTE")}")
    return navBackStackEntry?.arguments?.getString("KEY_ROUTE")
}