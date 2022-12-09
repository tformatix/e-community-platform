package at.fhooe.ecommunity.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument
import at.fhooe.ecommunity.R

/**
 * defines the different Screens
 * @param route navigation route
 * @param name name of the screen
 * @param icon icon for bottom nav
 * @param arguments optional arguments from one screen to another
 */
sealed class Screen(val route: String, val name: String, val icon: Int? = null, val arguments: List<NamedNavArgument>? = null) {

    /* Bottom */
    object Home : Screen("home", "Home", R.drawable.ic_home)
    object ECommunity : Screen("ecommunity", "Community", R.drawable.ic_e_community)
    object Sharing : Screen("sharing", "Sharing", R.drawable.ic_sharing)
    object Profile : Screen("profile", "Profile", R.drawable.ic_profile)

    /* StartUp */
    object StartUp : Screen("startup", "StartUp")
    object Login : Screen(
        "login", "Login",
        arguments = listOf(
            navArgument("email"){ defaultValue = "" },
            navArgument("password"){ defaultValue = "" },
        )
    )
    object Register : Screen(
        "register", "Register",
        arguments = listOf(
            navArgument("email"){ defaultValue = "" },
            navArgument("password"){ defaultValue = "" },
        )
    )
    object ForgotPassword : Screen(
        "forgotPassword", "Forgot Password",
        arguments = listOf(
            navArgument("email"){ defaultValue = "" },
        )
    )

    fun getRouteWithArguments(): String{
        if(arguments != null){
            var argRoute = "$route?"
            arguments.forEach { argument ->
                argRoute = "$argRoute${argument.name}={${argument.name}}&"
            }
            return argRoute.removeSuffix("&")
        }
        return route
    }

    /* Pairing Workflow */
    object PairingDiscovery: Screen("pairing/discovery", "Discover", null)
    object PairingSummary: Screen("pairing/summary", "Summary", null)
    object PairingAddNetwork: Screen("pairing/add_network", "Add Network", null)
    object PairingWifiConnect: Screen("pairing/wifi_connect", "Wifi Connect", null)

    /* Search */
    object NewsSearch: Screen("news/search", "Search", null)
}
