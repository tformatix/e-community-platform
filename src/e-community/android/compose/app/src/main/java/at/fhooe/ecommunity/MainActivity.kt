package at.fhooe.ecommunity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.screen.e_community.ECommunityScreen
import at.fhooe.ecommunity.ui.screen.home.HomeScreen
import at.fhooe.ecommunity.ui.screen.profile.ProfileScreen
import at.fhooe.ecommunity.ui.screen.home.HomeViewModel
import at.fhooe.ecommunity.ui.screen.home.search.SearchFilterScreen
import at.fhooe.ecommunity.ui.screen.home.search.SearchScreen
import at.fhooe.ecommunity.ui.screen.home.search.SearchViewModel
import at.fhooe.ecommunity.ui.screen.profile.ProfileViewModel
import at.fhooe.ecommunity.ui.screen.profile.pairing.*
import at.fhooe.ecommunity.ui.theme.ECommunityTheme

/**
 * Activity for Main user experience
 * @see ComponentActivity (compose activity)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    Scaffold(
        bottomBar = {
            AppBottomNavigation(navController, bottomNavItems)
        }
    )
    { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainScreenNavigationConfigurations(navController)
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
    navController: NavHostController
) {
    // start with home screen
    val startDestination = Screen.Home.route
    val application = ((LocalContext.current as MainActivity).application as ECommunityApplication)

    NavHost(navController, startDestination = startDestination) {
        /* Bottom Nav */
        composable(Screen.Home.route) {
            HomeScreen(HomeViewModel(application), navController)
        }
        composable(Screen.ECommunity.route) {
            ECommunityScreen()
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
                icon = { screen.icon?.let { painterResource(id = it) }?.let {
                    Icon(
                        painter = it,
                        contentDescription = "Icons",
                        tint = Color.White
                    )
                } },
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
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
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