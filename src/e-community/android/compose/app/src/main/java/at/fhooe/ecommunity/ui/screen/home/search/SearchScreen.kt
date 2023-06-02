package at.fhooe.ecommunity.ui.screen.home.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ECommunityDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MemberDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator

/**
 * Screen for Search (users and eCommunities)
 * @param _viewModel viewModel for NewsScreen
 * @param _navController navController for navigation back to News
 * @see Composable
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(_viewModel: SearchViewModel, _navController: NavHostController) {

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()

    // remember states
    val userSearchResults = _viewModel.mUserSearchResults.collectAsState()
    val eCommSearchResults = _viewModel.mECommSearchResults.collectAsState()

    val searchQuery = remember {
        mutableStateOf(
            SearchQuery("",
            userSearch = true,
            eCommSearch = true
    )
        )}
    val searchLayout = remember {
        mutableStateOf(
            SearchLayout(
            expandedUserView = false,
            expandedECommView = false
        )
        )
    }

    var isLoading = false

    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading = false
            _viewModel.backToIdle()
        }
        LoadingState.State.RUNNING -> {
            Log.d(TAG, "loading")
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.FAILED -> {
            //
        }
        else -> {}
    }

    Scaffold(
        topBar = { TopBarSearch(searchLayout, searchQuery, _viewModel, _navController) }
    ){
        // load search results
        Box() {
            SearchResults(searchQuery, searchLayout, userSearchResults, eCommSearchResults, _navController)
        }
    }
}

/**
 * display search results of the query and filters
 * if user & eComm filter is active split screen in 2 Rows
 * else use whole screen for each type
 */
@Composable
fun SearchResults(
    _searchQuery: MutableState<SearchQuery>,
    _searchLayout: MutableState<SearchLayout>,
    _userSearchResults: State<List<MemberDto>>,
    _eCommSearchResults: State<List<ECommunityDto>>,
    _navController: NavHostController
) {

    val searchQuery = _searchQuery.value

    // print results only when user entered a query
    searchQuery.query?.let {

        if (it.isNotEmpty()) {
            if (searchQuery.userSearch && searchQuery.eCommSearch) {
                // display all results
                AllSearchResults(_searchLayout, _userSearchResults, _eCommSearchResults, _navController)
            }
            else if (searchQuery.userSearch && !searchQuery.eCommSearch) {
                // users only
                UserSearchResults(_searchLayout, _userSearchResults, _navController)
            }
            else if (!searchQuery.userSearch && searchQuery.eCommSearch) {
                // eComms only
                ECommSearchResults(_searchLayout, _eCommSearchResults, _navController)
            }
            else {
                // everything filtered out -> display all
                AllSearchResults(_searchLayout, _userSearchResults, _eCommSearchResults, _navController)
            }
        }
    }
}

/**
 * if no filter is selected, display all results
 * @param _userSearchResults found users
 * @see Composable
 */
@Composable
fun AllSearchResults(
    _searchLayout: MutableState<SearchLayout>,
    _userSearchResults: State<List<MemberDto>>,
    _eCommSearchResults: State<List<ECommunityDto>>,
    _navController: NavHostController
) {
    Column(modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // divide screen
        UserSearchResults(_searchLayout, _userSearchResults, _navController)
        ECommSearchResults(_searchLayout, _eCommSearchResults, _navController)
    }
}

/**
 * displays the results for the users
 * @param _userSearchResults found users
 * @see Composable
 */
@Composable
fun UserSearchResults(_searchLayout: MutableState<SearchLayout>, _userSearchResults: State<List<MemberDto>>, _navController: NavHostController) {
    if (!_searchLayout.value.expandedECommView) {
        ResultTemplate(_searchLayout, stringResource(R.string.people_filter), _userSearchResults.value, _navController)
    }
}

/**
 * displays the results for the eCommunities
 * @param _eCommSearchResults found eCommunities
 * @see Composable
 */
@Composable
fun ECommSearchResults(_searchLayout: MutableState<SearchLayout>, _eCommSearchResults: State<List<ECommunityDto>>, _navController: NavHostController) {
    if (!_searchLayout.value.expandedUserView) {
        ResultTemplate(_searchLayout, stringResource(R.string.ecommunities_filter), _eCommSearchResults.value, _navController)
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ResultTemplate(_searchLayout: MutableState<SearchLayout>, _category: String, _items: List<Any>, _navController: NavHostController) {

    val isExpandedView = _searchLayout.value.expandedUserView || _searchLayout.value.expandedECommView

    Box {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {

            Column() {
                Text(
                    modifier = Modifier
                        .padding(5.dp),
                    text = _category,
                    fontSize = MaterialTheme.typography.h6.fontSize
                )

                // expanedView -> show all result in a scrollable list
                if (isExpandedView) {
                    LazyColumn {
                        items(_items) { item ->
                            ResultUserSearch(item, _navController)
                        }
                    }
                }
                else {
                    LazyColumn {
                        items(_items.take(Constants.VIEW_MORE_RESULTS)) { item ->
                            ResultUserSearch(item, _navController)
                        }
                    }
                }
            }

            // nothing found
            if (!_items.any()) {
                Column(modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 40.dp)) {

                    Text(
                        text = stringResource(R.string.nothing_found),
                        fontSize = MaterialTheme.typography.subtitle1.fontSize
                    )
                }
            }

            // view more or view less
            if (_items.size > Constants.VIEW_MORE_RESULTS) {
                Column(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(top = 5.dp)) {

                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colors.primary, // standard blue
                                )
                            ) {
                                if (isExpandedView) {
                                    append(stringResource(R.string.view_less_results))
                                }
                                else {
                                    append(stringResource(R.string.view_more_results))
                                }
                            }
                        },
                        onClick = {
                            if (!isExpandedView) {
                                if (_items.first() is MemberDto) {
                                    _searchLayout.value = SearchLayout(
                                        expandedUserView = true,
                                        expandedECommView = false
                                    )
                                }
                                else {
                                    _searchLayout.value = SearchLayout(
                                        expandedUserView = false,
                                        expandedECommView = true
                                    )
                                }
                            }
                            else {
                                _searchLayout.value = SearchLayout(
                                    expandedUserView = false,
                                    expandedECommView = false
                                )
                            }
                        },
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * template for each
 */
@Composable
fun ResultUserSearch(_item: Any, _navController: NavHostController) {
    Row(modifier = Modifier.clickable {

            // navigate to user specific page
            if (_item is MemberDto) {
                _navController.navigate("${Screen.SearchProfile.route}?memberId=${_item.id}")
            }
        }
    ) {
        // image
        Image(
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(50)),
            painter = if (isSystemInDarkTheme()) {
                painterResource(R.drawable.ic_default_profile_pic_white)
            } else {
                painterResource(R.drawable.ic_default_profile_pic_black)
            },
            contentDescription = "profile_image"
        )

        Column() {
            // user result
            if (_item is MemberDto) {
                _item.userName?.let {
                    Text(
                        text = it
                    )
                }
            }
            // eComm result
            else if (_item is ECommunityDto) {
                _item.name?.let {
                    Text(
                        text = it
                    )
                }
            }

        }
    }
}

/**
 * search text field
 * @see Composable
 */
@Composable
fun TopBarSearch(
    _searchLayout: MutableState<SearchLayout>,
    _searchQuery: MutableState<SearchQuery>,
    _viewModel: SearchViewModel,
    _navController: NavHostController
) {
    val query = rememberSaveable { mutableStateOf("") }

    val colorSelected = colorResource(R.color.blue)

    // search filters
    val peopleSearchActive = remember { mutableStateOf(colorSelected) }
    val eCommSearchActive = remember { mutableStateOf(colorSelected) }

    Column() {
        TopAppBar(
            title = { Text(text = "")},
            actions = {

                Row() {
                    // go back to news screen
                    IconButton(onClick = { _navController.navigateUp() }) {
                        Icon(
                            painterResource(R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }

                    // search text field
                    TextField(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .weight(1f),
                        value = query.value,
                        onValueChange = { onQueryChanged ->
                            run {
                                query.value = onQueryChanged

                                // show filters and make the request
                                if (onQueryChanged.isNotEmpty()) {

                                    // create new searchQuery
                                    _searchQuery.value = SearchQuery(
                                        query.value,
                                        peopleSearchActive.value == colorSelected,
                                        eCommSearchActive.value == colorSelected
                                    )

                                    _viewModel.makeQuery(_searchQuery)
                                }
                            }
                        },
                        placeholder = {
                            Text(text = stringResource(R.string.search))
                        },
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.subtitle1,
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                painterResource(R.drawable.ic_search),
                                contentDescription = "search"
                            )
                        }
                    )

                    // filter search results
                    IconButton(onClick = { _navController.navigate(Screen.SearchFilter.route) }) {
                        Icon(
                            painterResource(R.drawable.ic_filter),
                            contentDescription = "filter"
                        )
                    }
                }
            }
        )

        // search filters
        SearchFilters(_searchLayout, _searchQuery, peopleSearchActive, eCommSearchActive)
    }
}

/**
 * the search can be filtered for users and eCommunities
 * @param _peopleSearchActive filter for users
 * @param _eCommSearchActive filter for eCommunities
 * @see Composable
 */
@Composable
fun SearchFilters(_searchLayout: MutableState<SearchLayout>, _searchQuery: MutableState<SearchQuery>, _peopleSearchActive: MutableState<Color>, _eCommSearchActive: MutableState<Color>) {
    val colorSelected = colorResource(R.color.blue)
    val colorUnSelected = MaterialTheme.colors.background

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center) {

        // people filter
        Box(modifier = Modifier
            .border(BorderStroke(2.dp, _peopleSearchActive.value))
            .clickable {

                // set correct color of filter
                if (_peopleSearchActive.value == colorUnSelected) {
                    _peopleSearchActive.value = colorSelected
                } else {
                    _peopleSearchActive.value = colorUnSelected
                }

                _searchQuery.value = SearchQuery(
                    _searchQuery.value.query,
                    _peopleSearchActive.value == colorSelected,
                    _searchQuery.value.eCommSearch
                )

                // set expandedView to false -> display others
                _searchLayout.value = SearchLayout(
                    expandedUserView = false,
                    expandedECommView = _searchLayout.value.expandedECommView
                )
            }) {
            Text(modifier = Modifier
                .padding(all = 5.dp),
                text = stringResource(R.string.people_filter)
            )
        }


        // eCommunities filter
        Box(modifier = Modifier
            .padding(start = 10.dp)
            .border(BorderStroke(2.dp, _eCommSearchActive.value))
            .clickable {
                if (_eCommSearchActive.value == colorUnSelected) {
                    _eCommSearchActive.value = colorSelected
                } else {
                    _eCommSearchActive.value = colorUnSelected
                }

                _searchQuery.value = SearchQuery(
                    _searchQuery.value.query,
                    _searchQuery.value.userSearch,
                    _eCommSearchActive.value == colorSelected
                )

                // set expandedView to false -> display others
                _searchLayout.value = SearchLayout(
                    expandedUserView = _searchLayout.value.expandedUserView,
                    expandedECommView = false
                )
            }) {
            Text(modifier = Modifier
                .padding(all = 5.dp),
                text = stringResource(R.string.ecommunities_filter)
            )
        }
    }

    Divider(modifier = Modifier
        .padding(top = 5.dp),
        color = Color.Gray,
        thickness = 1.dp
    )
}
