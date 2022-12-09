package at.fhooe.ecommunity.ui.screen.home.search

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
            text = stringResource(R.string.sort_by),
            Modifier.padding(start = 10.dp),
            style = MaterialTheme.typography.h5,
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            SortByItem(R.drawable.ic_distance, stringResource(R.string.sort_by_distance))
            SortByItem(R.drawable.ic_entry_date, stringResource(R.string.sort_by_entry_date))
        }
    }
}

@Composable
fun SortByItem(_iconId: Int, _description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                            append(stringResource(R.string.apply_filter))
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