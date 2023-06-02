package at.fhooe.ecommunity.ui.screen.home.search

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.R

/**
 * Screen for Search Filters (sortBy, people in household, ...)
 * @param _navController navController for navigation back to Search
 * @see Composable
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchFilterScreen(_viewModel: SearchViewModel, _navController: NavHostController) {


    Scaffold(
        topBar = { TopBarSearchFilter(_navController) }
    ) {
        // load search results
        Box() {
            Filters()
        }
    }
}

@Composable
fun Filters() {
    Column() {
        Text(
            text = stringResource(R.string.search_filter_sort_by),
            Modifier.padding(start = 10.dp),
            style = MaterialTheme.typography.h5,
        )

        Box(
            modifier = Modifier
                .padding(top = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth()) {

                    SortByItem(R.drawable.ic_sortby_recommended, stringResource(R.string.search_filter_sortby_recommended))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth()) {

                    SortByItem(R.drawable.ic_sortby_distance, stringResource(R.string.search_filter_sortby_distance))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxWidth()) {

                    SortByItem(R.drawable.ic_sortby_entry_date, stringResource(R.string.search_filter_sortby_entry_date))
                }
            }
        }
    }
}

@Composable
fun SortByItem(_iconId: Int, _description: String) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Column(
        ) {
            Icon(
                painterResource(_iconId),
                contentDescription = _description,
                Modifier.padding(start = 15.dp)
            )

            Text(
                text = _description
            )
        }
    }
}

@Composable
fun TopBarSearchFilter(_navController: NavHostController) {

    Column() {
        TopAppBar(
            title = { Text(text = "") },
            navigationIcon = {
                IconButton(onClick = { _navController.navigateUp() }) {
                    Icon(
                        painterResource(R.drawable.ic_close),
                        contentDescription = "Back"
                    )
                }},
            actions = {
                ClickableText(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.White,
                            )
                        ) {
                            append(stringResource(R.string.search_filter_apply_filter))
                        }
                    },
                    Modifier.padding(end = 10.dp),
                    onClick = {
                        _navController.navigateUp()
                    }
                )
            }
        )
    }
}