package at.fhooe.ecommunity.ui.screen.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.navigation.Screen

/**
 * Screen for News (news from household and eCommunity)
 * @param _viewModel viewModel for NewsScreen
 * @param _navController navController for navigation to Search
 * @see Composable
 */
@Composable
fun NewsScreen(_viewModel: NewsViewModel, _navController: NavHostController) {
    Scaffold(
        topBar = { TopBarNews(_navController) }
    ) {
        // load news
    }
}

/**
 * search for users and eCommunities
 * @see Composable
 */
@Composable
fun TopBarNews(_navController: NavHostController) {

    TopAppBar(
        title = { Text(text = "") },
        actions = {
            IconButton(onClick = { _navController.navigate(Screen.NewsSearch.route) }) {
                Icon(painterResource(R.drawable.ic_search), "Search")
            }
        }
    )
}