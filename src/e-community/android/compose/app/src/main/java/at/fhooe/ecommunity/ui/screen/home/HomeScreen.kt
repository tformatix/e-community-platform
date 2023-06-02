package at.fhooe.ecommunity.ui.screen.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.local.entity.Tile
import at.fhooe.ecommunity.data.local.setup.DashboardManager
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import at.fhooe.ecommunity.data.remote.signalr.dto.MeterDataRTDto
import at.fhooe.ecommunity.ui.component.LifecycleListener
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.model.RemoteException
import at.fhooe.ecommunity.navigation.Screen
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import at.fhooe.ecommunity.util.ECommunityFormatter
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Screen for Home (display Realtime & History data)
 * @param _viewModel viewModel for HomeScreen
 * @param _navController navController for navigation to other Screens
 * @see Composable
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(_viewModel: HomeViewModel, _navController: NavHostController) {

    val coroutineScope = rememberCoroutineScope()

    // collect viewModel state
    val state by _viewModel.mState.collectAsState()
    var isLoading = true

    // setup tiles (insert into room)
    val dashboardManager = DashboardManager(_viewModel.getApplication(), _viewModel.getApplication().tileRepository)

    // collect changes from the tileRepository
    val tiles: List<Tile> by _viewModel.getApplication().tileRepository.getTiles().collectAsState(initial = emptyList())

    // timer for extend
    val extendTimer = remember { mutableStateOf(Timer()) }
    val timerCount = remember { mutableStateOf(0) }

    when(state.mState) {
        LoadingState.State.SUCCESS -> {
            isLoading = false
            _viewModel.backToIdle()
        }
        LoadingState.State.RUNNING -> {
            LoadingIndicator()
            isLoading = true
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            _viewModel.backToIdle() // bring the view model back to the idle state
            Log.d(TAG, "error")

            if (state.mException == null) {
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(
                        RemoteException(RemoteException.Type.NO_INTERNET)
                    ),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
            state.mException?.let {
                val remoteException = _viewModel.mApplication.remoteExceptionRepository.exceptionToRemoteException(it)
                Toast.makeText(
                    _viewModel.mApplication,
                    _viewModel.mApplication.remoteExceptionRepository.remoteExceptionToString(RemoteException(RemoteException.Type.NO_INTERNET)),
                    Toast.LENGTH_SHORT
                ).show() // show error message
            }
        }
        else -> {}
    }

    // default RT meterData
    val meterDataRTDto = remember {
        mutableStateOf(
            BufferedMeterDataRTDto(
                "",
                0,
                0,
                0,
                0,
                0,
                0,
                listOf(MeterDataRTDto(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    0,
                    0,
                    0,
                    0,
                ))
            )
        )
    }

    // be aware of lifecycle events for signalR (Start/Stop)
    LifecycleListener {
        when(it) {
            Lifecycle.Event.ON_RESUME -> {

                // init signalRListener
                initSignalR(_viewModel, meterDataRTDto)
            }

            Lifecycle.Event.ON_STOP -> {

                // stop signalr
                stopSignalR(_viewModel)
                extendTimer.value.cancel()
            }

            Lifecycle.Event.ON_DESTROY -> {
                // stop signalr
                stopSignalR(_viewModel)
                extendTimer.value.cancel()
            }
            else -> {}
        }
    }

    // only setup tiles once (true does not change)
    LaunchedEffect(true) {
        coroutineScope.launch(Dispatchers.Default) {
            dashboardManager.setupTilesHome()
        }

        checkConnection(extendTimer, meterDataRTDto, timerCount, _viewModel)
    }

    // if meterData has been changed, (re)composite the UI
    if (meterDataRTDto.value.timestamp != "") {
        TileGridView(isLoading, tiles, meterDataRTDto)
    }

    // build screen (TopBar and GridLayout)
    Scaffold(
        topBar = { TopBarHome(_viewModel, _navController) }
    ) {
        TileGridView(isLoading, tiles, meterDataRTDto)
    }
}

/**
 * draw a tile
 * different layout for large and small tile (tile.isLargeTile)
 * @param _tile Tile to draw
 * @param _meterDataRTDto current meterData
 * @see Composable
 */
@Composable
fun TileCard(_tile: Tile, _meterDataRTDto: BufferedMeterDataRTDto) {

    Column {
        // region (household, community)
        Text(
            text = _tile.region,
            fontSize = 22.sp
        )

        // action (consumption, feed_id)
        Text(
            text = _tile.action,
            fontSize = 20.sp
        )

        var value = 0
        var color = colorResource(id = R.color.value_normal)

        when(_tile.tileId) {
            LocalContext.current.getString(R.string.tile_id_community_consumption) -> {
                value = _meterDataRTDto.eCommunityActivePowerPlus
            }

            LocalContext.current.getString(R.string.tile_id_household_consumption) -> {
                value = _meterDataRTDto.meterDataMember?.sumOf { it.activePowerPlus } ?: 0
            }

            LocalContext.current.getString(R.string.tile_id_household_feed_in) -> {
                value = _meterDataRTDto.meterDataMember?.sumOf { it.activePowerMinus } ?: 0
                if (value > 0) { color = colorResource(id = R.color.value_good) }
            }

            LocalContext.current.getString(R.string.tile_id_community_feed_in) -> {
                value = _meterDataRTDto.eCommunityActivePowerMinus
                if (value > 0) { color = colorResource(id = R.color.value_good) }
            }
        }

        val formatter = ECommunityFormatter(LocalContext.current)

        // value
        Text(
            text = formatter.formatSmartMeterValue(value),
            fontSize = 34.sp,
            color = color
        )
    }
}

/**
 * creates a grid layout for the tiles
 * @param _isLoading check if page is loading
 * @param _tiles list of tiles
 * @param _meterDataRTDto current meterData
 * @see Composable
 *
 */
@Composable
fun TileGridView(_isLoading: Boolean, _tiles: List<Tile>, _meterDataRTDto: MutableState<BufferedMeterDataRTDto>) {
    Column(
        // for this column we are adding a
        // modifier to it to fill max size.
        modifier = Modifier
            .gesturesDisabled(_isLoading)
            .fillMaxSize()
    ) {
        // on below line we are creating a column
        // for each item of our staggered grid.
        Column(
            // in this column we are adding modifier to it
            // and adding padding from all sides and vertical scroll.
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(5.dp)
        ) {
            // on below line we are calling our
            // custom staggered vertical grid item.
            CustomStaggeredVerticalGrid(
                // on below line we are specifying
                // number of columns for our grid view.
                _numColumns = 2,

                // on below line we are adding padding
                // from all sides for our grid view.
                _modifier = Modifier.padding(5.dp)
            ) {
                // inside staggered grid view we are
                // adding images for each item of grid.
                _tiles.forEach { tile ->
                    // on below line inside our grid
                    // item we are adding card.
                    Card(
                        // on below line inside the card we
                        // are adding modifier to it to specify
                        // max width, padding, elevation and shape for the card
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        // on below line we are adding column inside our card.
                        Column(
                            // in this column we are adding modifier
                            // to fill max size and align our
                            // card center horizontally.
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.CenterHorizontally),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // draw tile
                            TileCard(tile, _meterDataRTDto.value)
                        }
                    }
                }
            }
        }
    }
}

/**
 * staggered grid layout manager implementation
 * @see Composable
 */
@Composable
fun CustomStaggeredVerticalGrid(
    // on below line we are specifying
    // parameters as modifier, num of columns
    _modifier: Modifier = Modifier,
    _numColumns: Int = 2,
    _content: @Composable () -> Unit
) {
    // inside this grid we are creating
    // a layout on below line.
    Layout(
        // on below line we are specifying
        // content for our layout.
        content = _content,
        // on below line we are adding modifier.
        modifier = _modifier
    ) { measurable, constraints ->
        // on below line we are creating a variable for our column width.
        val columnWidth = (constraints.maxWidth / _numColumns)

        // on the below line we are creating and initializing our items constraint widget.
        val itemConstraints = constraints.copy(maxWidth = columnWidth)

        // on below line we are creating and initializing our column height
        val columnHeights = IntArray(_numColumns) { 0 }

        // on below line we are creating and initializing placebles
        val placeables = measurable.map { measurable ->
            // inside placeble we are creating
            // variables as column and placebles.
            val column = testColumn(columnHeights)
            val placeable = measurable.measure(itemConstraints)

            // on below line we are increasing our column height/
            columnHeights[column] += placeable.height
            placeable
        }

        // on below line we are creating a variable for
        // our height and specifying height for it.
        val height =
            columnHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
                ?: constraints.minHeight

        // on below line we are specifying height and width for our layout.
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // on below line we are creating a variable for column y pointer.
            val columnYPointers = IntArray(_numColumns) { 0 }

            // on below line we are setting x and y for each placeable item
            placeables.forEach { placeable ->
                // on below line we are calling test
                // column method to get our column index
                val column = testColumn(columnYPointers)

                placeable.place(
                    x = columnWidth * column,
                    y = columnYPointers[column]
                )

                // on below line we are setting
                // column y pointer and incrementing it.
                columnYPointers[column] += placeable.height
            }
        }
    }
}

/**
 * check if column if a above a minHeight (more space in grid view)
 * @param _columnHeights check for minHeight
 * @return return columnIndex
 */
private fun testColumn(_columnHeights: IntArray): Int {
    // on below line we are creating a variable for min height.
    var minHeight = Int.MAX_VALUE

    // on below line we are creating a variable for column index.
    var columnIndex = 0

    // on below line we are setting column  height for each index.
    _columnHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            columnIndex = index
        }
    }
    // at last we are returning our column index.
    return columnIndex
}


/**
 * init signal R and request RT data
 * @param _viewModel viewModel for Home Screen
 * @param _meterDataRTDto current meterData
 */
fun initSignalR(_viewModel: HomeViewModel, _meterDataRTDto: MutableState<BufferedMeterDataRTDto>) {
    Log.d(TAG, "request START RT data")
    _viewModel.requestRTDataStart(_meterDataRTDto)
}

/**
 * stop signal R and request RT stop
 * @param _viewModel viewModel for Home Screen
 */
fun stopSignalR(_viewModel: HomeViewModel) {
    Log.d(TAG, "request STOP RT data")
    _viewModel.requestRTDataStop()
}

/**
 * topBar has a switch for realtime/history and a filter function
 * @see Composable
 */
@Composable
fun TopBarHome(_viewModel: HomeViewModel, _navController: NavHostController) {
    val isRealtimeActive = remember { mutableStateOf(true) }

    TopAppBar(
        title = { Text(text = if (isRealtimeActive.value)
                        { LocalContext.current.getString(R.string.dashboard_realtime) }
                   else { LocalContext.current.getString(R.string.dashboard_history)})
                },
        actions = {

            IconButton(onClick = {
                _viewModel.requestRTDataStop()
                _navController.navigate(Screen.Search.route)
            }) {
                Icon(painterResource(R.drawable.ic_search), "Search")
            }

            // switch between realtime & history
            IconButton(onClick = { isRealtimeActive.value = !isRealtimeActive.value }) {
                Icon( if (isRealtimeActive.value)
                         { painterResource(R.drawable.ic_history) }
                    else { painterResource(R.drawable.ic_realtime) }, "")
            }

            // filter icon
            IconButton(onClick = { /* FILTER */}) {
                Icon(painterResource(R.drawable.ic_filter), "")
            }
        }
    )
}

/**
 * check connection to signal R
 */
fun checkConnection(
    _extendTimer: MutableState<Timer>,
    _meterDataRTDto: MutableState<BufferedMeterDataRTDto>,
    _timerCount: MutableState<Int>,
    _viewModel: HomeViewModel
) {
    _extendTimer.value.schedule(object : TimerTask() {
        override fun run() {

            if (_viewModel.getConnectionState() != HubConnectionState.CONNECTED) {
                Log.e(TAG, "lost SignalR connection")
                CoroutineScope(Dispatchers.Default).launch {
                    _viewModel.mState.emit(
                        LoadingState(
                            LoadingState.State.FAILED,
                            mException = null))
                }


                // reset tile => user know no data is coming
                //setTilesZero()
                _meterDataRTDto.value = BufferedMeterDataRTDto(
                    "",
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    listOf(MeterDataRTDto(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        0,
                        0,
                        0,
                        0,
                    ))
                )

                // start signalR
                _viewModel.requestRTDataStart(_meterDataRTDto, false)
            }
            else {
                Log.d(TAG, "connected")
            }

            if (++_timerCount.value == Constants.TIMER_COUNT) {
                _viewModel.requestRTDataExtend()
                _timerCount.value = 0
            }

            checkConnection(
                _extendTimer,
                _meterDataRTDto,
                _timerCount,
                _viewModel
            )
        }
    }, Constants.CHECK_SIGNALR_TIMER.toLong())
}

