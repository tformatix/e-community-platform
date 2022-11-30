package at.fhooe.smartmeter.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.SmartMeterApplication
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.adapters.TileAdapter
import at.fhooe.smartmeter.adapters.TileMoveCallback
import at.fhooe.smartmeter.database.TileViewModel
import at.fhooe.smartmeter.database.TileViewModelFactory
import at.fhooe.smartmeter.databinding.FragmentHomeBinding
import at.fhooe.smartmeter.dto.MeterDataRTDto
import at.fhooe.smartmeter.models.LocalDeviceConfig
import at.fhooe.smartmeter.models.Tile
import at.fhooe.smartmeter.navigation.IFragmentMessageService
import at.fhooe.smartmeter.services.SignalRListener
import at.fhooe.smartmeter.services.SignalRManager
import at.fhooe.smartmeter.util.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.openapitools.client.apis.SmartMeterApi
import java.util.*


class HomeFragment : Fragment(), SignalRManager, TileAdapter.TileDragAndDropListener {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var mIFragmentMessageService: IFragmentMessageService
    private lateinit var mTileAdapter: TileAdapter
    private lateinit var mTextFormatter: TextFormatter

    private var mSignalRListener: SignalRListener? = null
    private var mContext: Context? = null
    private var mSignalRTimerCounter = 0
    private var mSignalRTimer: Timer? = null

    // do not update recyclerview when a drag of tiles was started
    private var mUpdateSignalR = true

    private val mTileViewModel: TileViewModel by viewModels {
        TileViewModelFactory((requireActivity().application as SmartMeterApplication).tileRepository)
    }

    private val mBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mSignalRTimerCounter = 0
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        // start refreshing layout
        mBinding.fragmentHomeSwipeRefresh.isRefreshing = true

        mTileAdapter = TileAdapter(mTileViewModel)
        mTileAdapter.setTileDragAndDropListener(this)

        val tileTouchCallback: ItemTouchHelper.Callback = TileMoveCallback(mTileAdapter)
        val tileTouchHelper = ItemTouchHelper(tileTouchCallback)

        // setup dashboard
        with(mBinding.fragmentDashboardRecyclerViewTiles) {
            setHasFixedSize(true)
            tileTouchHelper.attachToRecyclerView(this)
            adapter = mTileAdapter
            layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }
        }

        // generate tiles
        val dashboardManager = DashboardManager(requireContext(), mTileViewModel)
        dashboardManager.setupTilesHome()

        // update tiles
        lifecycle.coroutineScope.launch {
            mTileViewModel.getTiles().collect {
                mTileAdapter.setTiles(it)
            }
        }

        mContext?.let {
            mTextFormatter = TextFormatter(it)
        }

        return mBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_home_menu, menu)

        val menuItem = menu.getItem(0)

        mContext?.let {
            if (HomeConfiguration.getActiveDataMode() == ActiveDataMode.REALTIME) {
                menuItem.icon = ContextCompat.getDrawable(it, R.drawable.ic_history)
            }
            else {
                menuItem.icon = ContextCompat.getDrawable(it, R.drawable.ic_realtime)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.main_home_menu_switch -> {
                var activeDataMode = HomeConfiguration.getActiveDataMode()
                var activeDataModeTitle = ""

                if (activeDataMode == ActiveDataMode.REALTIME) {
                    activeDataMode = ActiveDataMode.HISTORY
                    activeDataModeTitle = mContext?.getString(R.string.history).toString()
                } else {
                    activeDataMode = ActiveDataMode.REALTIME
                    activeDataModeTitle = mContext?.getString(R.string.realtime).toString()
                }

                requireActivity().invalidateOptionsMenu()
                HomeConfiguration.setActiveDataMode(activeDataMode)
                Navigation.findNavController(mBinding.root).currentDestination?.label = activeDataModeTitle
                mIFragmentMessageService.onCommunicate(Constants.SHOW_HOME, null)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mIFragmentMessageService = context as IFragmentMessageService
    }

    /**
     * stop requesting real time data
     */
    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    /**
     * stop signalR and realtime data
     */
    override fun onPause() {
        super.onPause()

        mSignalRTimer?.cancel()

        // stop signalR
        stopSignalR()
        val i = 5
    }

    /**
     * start signalR again
     */
    override fun onResume() {
        super.onResume()

        // start signalR
        mSignalRTimer?.cancel()
        initSignalR()
    }

    /**
     * remove viewBinding
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * start signal R listener and update tile values
     */
    override fun initSignalR() {
        // request real time data
        mContext?.let {
            Api.authorizedBackendCall(it, null) {

                // start signal R
                startSignalR(it)

                if (!LocalDeviceConfig.getSignalRRequested()) {
                    // request real time data from cloud
                    val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                    try {
                        val ok = smartMeterApi.smartMeterRequestRTDataGet()
                        ok.status?.let {
                            Log.d(TAG, "status of request RT data: ${ok.status}")
                        }

                        LocalDeviceConfig.setSignalRRequested(true)

                        checkConnection()
                    }
                    catch (e: Exception) {
                        Log.e(TAG, e.toString())

                        mContext?.let {
                            Snackbar.make(mBinding.fragmentDashboardRecyclerViewTiles, it.getString(R.string.connection_failed), Snackbar.LENGTH_SHORT)
                                .setAction(it.getString(R.string.retry)) { }
                                .show()
                        }
                    }
                }
            }
        }
    }

    /**
     * try to start SignalR connection
     * @return true(started), false(error)
     */
    override fun startSignalR(accessToken: String) {
        val connStr = Constants.HTTP_BASE_URL_CLOUD + Constants.SIGNALR_URL

        // get real time data listener
        mSignalRListener = SignalRListener(connStr, accessToken, Constants.SIGNALR_METHOD,
            Action1 { data ->
                val json = JSONObject(data.toMap()) // json object of server data
                val meterDataRTDto: MeterDataRTDto = Gson().fromJson(json.toString(), MeterDataRTDto::class.java)

                Log.d(TAG, "receiveData | State: ${mSignalRListener?.getConnectionState().toString()}")
                receiveData(meterDataRTDto)
            }
        )

        // start connection
        mSignalRListener?.let {
            val started = it.startConnection()

            if (started) {
                Log.d(TAG, "startSignalR() SignalR try starting")
            } else {
                Log.e(TAG, "startSignalR() SignalR not started")
            }
        }
    }

    /**
     * close SignalR connection
     */
    override fun stopSignalR() {

        Log.d(TAG, "close signalR & RT connection")
        mContext?.let {
            Api.authorizedBackendCall(it, null) {
                // stop request realtime data
                val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                try {
                    val ok = smartMeterApi.smartMeterStopRTDataGet()
                    ok.status?.let {
                        Log.d(TAG, "status of stop RT data: ${ok.status}")
                    }
                }
                catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }

        mSignalRListener?.stopConnection()
        LocalDeviceConfig.setSignalRRequested(false)
    }

    /**
     * receives realtime data from signal r
     * collect list of tiles and update its value
     * @param data MeterData Realtime
     */
    override fun receiveData(data: Any) {

        if (!mUpdateSignalR) {
            Log.d(TAG, "cannot update, tiles are dragging now")
            return
        }

        // we receive data, deactivate refreshing
        mBinding.fragmentHomeSwipeRefresh.isRefreshing = false

        val meterDataRT = data as MeterDataRTDto

        CoroutineScope(Dispatchers.Default).launch {
            // update meter data value only when view is in Start/Resume state
            whenStarted { updateMeterDataValues(meterDataRT) }
            whenResumed { updateMeterDataValues(meterDataRT) }
        }
    }

    /**
     * update real time data from smart meter
     * @param meterDataRT meterData values
     */
    private suspend fun updateMeterDataValues(meterDataRT: MeterDataRTDto) {
        mTileViewModel.getTiles().collect {
            val updatedList = it

            val householdConsumption: Tile? = updatedList.firstOrNull {
                    x -> x.tileId == mContext?.getString(R.string.tile_id_household_consumption)
            }

            val householdFeedIn: Tile? = updatedList.firstOrNull {
                    x -> x.tileId == mContext?.getString(R.string.tile_id_household_feed_in)
            }

            val communityConsumption: Tile? = updatedList.firstOrNull {
                    x -> x.tileId == mContext?.getString(R.string.tile_id_community_consumption)
            }

            val communityFeedIn: Tile? = updatedList.firstOrNull {
                    x -> x.tileId == mContext?.getString(R.string.tile_id_community_feed_in)
            }

            householdConsumption?.value = mTextFormatter.formatSmartMeterValue(meterDataRT.activePowerPlus)
            householdFeedIn?.value = mTextFormatter.formatSmartMeterValue(meterDataRT.activePowerMinus)
            communityConsumption?.value = mTextFormatter.formatSmartMeterValue(meterDataRT.eCommunityActivePowerPlus)
            communityFeedIn?.value = mTextFormatter.formatSmartMeterValue(meterDataRT.eCommunityActivePowerMinus)

            CoroutineScope(Dispatchers.Main).launch {
                if (meterDataRT.missingSmartMeterCountMember > 0) {
                    mContext?.let {
                        mBinding.fragmentHomeMissingSmartMeterMember.visibility = View.VISIBLE
                        mBinding.fragmentHomeMissingSmartMeterMember.text = "${it.getString(R.string.missing_smart_meter_member)} ${meterDataRT.missingSmartMeterCountMember}"
                    }
                }
                else {
                    mBinding.fragmentHomeMissingSmartMeterMember.visibility = View.GONE
                }

                if (meterDataRT.missingSmartMeterCount > 0) {
                    mContext?.let {
                        mBinding.fragmentHomeMissingSmartMeterEComm.visibility = View.VISIBLE
                        mBinding.fragmentHomeMissingSmartMeterEComm.text = "${it.getString(R.string.missing_smart_meter_e_comm)} ${meterDataRT.missingSmartMeterCount}"
                    }
                }
                else {
                    mBinding.fragmentHomeMissingSmartMeterEComm.visibility = View.GONE
                }

                mContext?.let {
                    mBinding.fragmentHomeTimestamp.text = "${it.getString(R.string.timestamp_meter_data)} ${mTextFormatter.formatTimestamp(meterDataRT.timestamp)}"
                }
            }

            Log.d(TAG, meterDataRT.toString())

            // update tiles on UI Thread
            CoroutineScope(Dispatchers.Main).launch {
                mTileAdapter.setTiles(updatedList)
            }
        }
    }

    /**
     * check if signalR connection is connected/extended
     */
    private fun checkConnection() {

        mSignalRTimer = Timer()

        mSignalRTimer?.schedule(object : TimerTask() {
            override fun run() {
                mSignalRListener?.let {
                    if(it.getConnectionState() == HubConnectionState.DISCONNECTED) {
                        Log.e(TAG, "lost SignalR connection")

                        // reset tile => user know no data is coming
                        setTilesZero()

                        mContext?.let {
                            Api.authorizedBackendCall(it, null) {
                                startSignalR(it)
                            }
                        }
                    }
                    else {
                        Log.d(TAG, "connected")
                    }
                }

                if (++mSignalRTimerCounter == Constants.TIMER_COUNT) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val smartMeterApi = SmartMeterApi(Constants.HTTP_BASE_URL_CLOUD)

                        try {
                            val ok = smartMeterApi.smartMeterExtendRTDataGet()
                            ok.status?.let {
                                Log.d(TAG, "status of extend RT data: ${ok.status}")
                            }
                        }
                        catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    }
                }

                checkConnection()
            }
        }, Constants.CHECK_SIGNALR_TIMER.toLong())
    }



    /**
     * set tile value to zero (0 W) because we lost connection to signalR
     */
    private fun setTilesZero() {
        CoroutineScope(Dispatchers.Default).launch {
            mTileViewModel.getTiles().collect {
                val updatedList = it

                updatedList.stream().forEach {  x -> x.value = "0 W" }

                // update tiles on UI Thread
                CoroutineScope(Dispatchers.Main).launch {
                    mTileAdapter.setTiles(updatedList)
                }
            }
        }
    }

    override fun dragStarted() {
        mUpdateSignalR = false
    }

    override fun dragFinished() {
        mUpdateSignalR = true
    }
}