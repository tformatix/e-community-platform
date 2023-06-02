package at.fhooe.smartmeter.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.Constants.TAG
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.api.SignalRListener
import at.fhooe.smartmeter.chart.RealtimeXAxisFormatter
import at.fhooe.smartmeter.chart.RealtimeYAxisFormatter
import at.fhooe.smartmeter.databinding.FragmentRealtimeBinding
import at.fhooe.smartmeter.dto.MeterDataDto
import at.fhooe.smartmeter.dto.MeterDataRTDto
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.microsoft.signalr.Action1
import org.json.JSONObject

const val TAG_RT: String = "RealtimeFragment"
const val RT_TIMEOUT: Long = 5 // seconds after Connection Timeout shows up
const val CHART_ENTRY_COUNT: Float = 30f // visible seconds

class RealtimeFragment : Fragment(), OnChartValueSelectedListener {
    private lateinit var binding: FragmentRealtimeBinding
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var signalRListener: SignalRListener // SignalR Listener
    private lateinit var taraValues: MeterDataDto<Double> // used for differences
    private lateinit var xAxisFormatter: RealtimeXAxisFormatter // formatter for x axis
    private lateinit var yAxisFormatter: RealtimeYAxisFormatter // formatter for y axis
    private lateinit var unit: String // unit used for highlight output
    private var prevMeterData: MeterDataRTDto? = null // last real time value
    private var currentMeterData: MeterDataRTDto? = null // current real time value
    private var chartInitialized: Boolean = false
    private var appState: Boolean = false // true if visible


    // ###################################  INIT    ###################################
    // ------- INITIALIZATION BASIC ------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPrefs = activity?.getSharedPreferences(getString(R.string.shared_prefs_settings), Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRealtimeBinding.inflate(inflater, container, false)
        initComponents()
        initSignalR()
        return binding.root
    }

    /**
     * initialize components (button, ...)
     */
    private fun initComponents() {
        Log.d(TAG, "$TAG_RT::initComponents()")
        binding.fragmentRealtimeChartRt.setNoDataText(getString(R.string.fragment_realtime_chart_loading)) // text if no chart data available
        binding.fragmentRealtimeChartRt.setNoDataTextColor(resources.getColor(R.color.color_default, null)) // color if no chart data available
        binding.fragmentRealtimeButtonTara.setOnClickListener {
            currentMeterData?.let {
                taraValues = it.meterDataValues.copy()
                binding.fragmentRealtimeTvTaraValue.text = "${getString(R.string.fragment_realtime_button_tara)} $it"
                clearChart()
            }
        }
        binding.fragmentRealtimeButtonResetTara.setOnClickListener {
            resetTara()
            binding.fragmentRealtimeTvTaraValue.text = ""
            clearChart()
        }
        resetTara()
    }

    /**
     * resets tara object
     */
    private fun resetTara() {
        taraValues = MeterDataDto<Double>(-1, "", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    /**
     * returns value which is considered (in our case: activePowerPlus)
     */
    private fun <T> getAppropriateMeterDataValue(meterDataDto: MeterDataDto<T>): T {
        return meterDataDto.activePowerPlus
    }

    /**
     * set value which is considered (in our case: activePowerPlus)
     */
    private fun <T> setAppropriateMeterDataValue(meterDataDto: MeterDataDto<T>, value: T) {
        meterDataDto.activePowerPlus = value
    }

    // ###################################  SignalR ###################################
    // ------- INITIALIZATION SIGNALR -------
    /**
     * initialize SignalR connection
     * - IP is stored in shared prefs
     * - URL and Method are in strings.xml
     */
    private fun initSignalR() {
        Log.d(TAG, "$TAG_RT::initSignalR()")
        // IP of server
        val ip = sharedPrefs.getString(getString(R.string.shared_prefs_settings_ip), "")
        // AES Key without spaces
        val aesKey: String = sharedPrefs.getString(getString(R.string.shared_prefs_settings_aeskey), "")!!.replace(" ", "")

        prevMeterData = null
        currentMeterData = null

        // Parse Json Response to MeterDataDto<T>
        val meterDataRTUnitType = object : TypeToken<MeterDataDto<String>>() {}.type
        val meterDataRTDoubleType = object : TypeToken<MeterDataDto<Double>>() {}.type

        // create Listener with url and action
        signalRListener = SignalRListener(resources.getString(R.string.backend_url_realtime, ip), "AES${aesKey}END",
                Action1 { data ->
                    val json = JSONObject(data.toMap()) // json object of server data
                    val unit = Gson().fromJson<MeterDataDto<String>>(json.getString("unit"), meterDataRTUnitType)
                    val values = Gson().fromJson<MeterDataDto<Double>>(json.getString("meterDataValues"), meterDataRTDoubleType)
                    val meterData = MeterDataRTDto(unit, values)
                    realTimeArrived(meterData)
                })
    }

    override fun onStart() {
        super.onStart()
        appState = true
        startSignalRListener()
    }

    /**
     * start SignalR listener and checks if data arrives (after RT_TIMEOUT)
     */
    private fun startSignalRListener() {
        if (signalRListener.startConnection()) {
            Log.d(TAG, "$TAG_RT::startSignalR() SignalR try starting")
            checkRealtimeConnection(prevMeterData)
        } else {
            Log.d(TAG, "$TAG_RT::startSignalR() SignalR not started")
        }
    }

    /**
     * checks if data arrived after RT_TIMEOUT
     */
    private fun checkRealtimeConnection(meterDataDto: MeterDataRTDto?) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentMeterData == null) {
                signalRError() // no data arrived yet
            } else if (meterDataDto != null && currentMeterData!!.meterDataValues.id == meterDataDto.meterDataValues.id) {
                signalRError() // no new data arrived since RT_TIMEOUT
            }
        }, RT_TIMEOUT * 1000) // to milliseconds
    }

    /**
     * some SignalR error occurred
     * --> snackbar with restart
     */
    private fun signalRError() {
        if (appState) { // only if app is visible
            Log.d(TAG, "$TAG_RT::signalRError()")
            startSignalRListener()
            activity?.runOnUiThread {
                Snackbar.make(binding.fragmentRealtimeChartRt, getString(R.string.fragment_realtime_snackbar_signalr_error), Snackbar.LENGTH_LONG)
                        .setAction(R.string.fragment_realtime_snackbar_reload) {
                            initSignalR()
                            startSignalRListener()
                            clearChart()
                        }
                        .show()
            }
        }
    }

    /**
     * stop SignalR listener
     */
    private fun stopSignalRListener() {
        if (signalRListener.stopConnection())
            Log.d(TAG, "$TAG_RT::startSignalR() SignalR try closing")
        else
            Log.d(TAG, "$TAG_RT::startSignalR() SignalR not closed")
    }

    override fun onStop() {
        super.onStop()
        appState = false
        stopSignalRListener()
    }

    // ------- HANDLING REALTIME VALUES -------
    /**
     * real time data arrived successfully
     * this method is called every second
     */
    private fun realTimeArrived(meterData: MeterDataRTDto) {
        checkRealtimeConnection(meterData)
        with(meterData) {
            // needed if don't reset after pressing tara (2 or more times tara) --> shouldn't take e.g 1 W rather 700 W
            currentMeterData = MeterDataRTDto(meterDataValues = meterDataValues.copy(), unit = unit)
            // tara value
            setAppropriateMeterDataValue(meterDataValues, getAppropriateMeterDataValue(meterDataValues) - getAppropriateMeterDataValue(taraValues))
            activity?.runOnUiThread {
                fillMeterData(meterData)
            }
        }
    }

    /**
     * fill components and chart (in UI Thread)
     * */
    private fun fillMeterData(meterData: MeterDataRTDto) {
        with(meterData) {
            try {
                compareWithPrevious(this)
                prevMeterData = MeterDataRTDto(meterDataValues = meterDataValues.copy(), unit = unit) // set previous
                binding.fragmentRealtimeTvCurrent.text = getAppropriateMeterDataValue(meterDataValues).toInt().toString()
                binding.fragmentRealtimeTvCurrentUnit.text = getAppropriateMeterDataValue(unit)
                binding.fragmentRealtimeTvTimestamp.text = "${meterDataValues.timestamp.split("T")[1]}"
                insertToChart(meterData)
            } catch (e: Exception) {
                Log.d(TAG, "$TAG_RT::fillMeterData() ${e}")
            }

        }
    }

    /**
     * compare with previous data for status view (arrow and color)
     */
    private fun compareWithPrevious(meterData: MeterDataRTDto) {
        if (prevMeterData != null) {
            var color: Int
            var drawable: Int
            if (getAppropriateMeterDataValue(meterData.meterDataValues) > getAppropriateMeterDataValue(prevMeterData!!.meterDataValues)) {
                color = resources.getColor(R.color.color_realtime_upward, null)
                drawable = R.drawable.ic_realtime_arrow_upward
            } else if (getAppropriateMeterDataValue(meterData.meterDataValues) < getAppropriateMeterDataValue(prevMeterData!!.meterDataValues)) {
                color = resources.getColor(R.color.color_realtime_downward, null)
                drawable = R.drawable.ic_realtime_arrow_downward
            } else {
                color = resources.getColor(R.color.color_realtime_text, null)
                drawable = R.drawable.ic_realtime_equal
            }
            binding.fragmentRealtimeTvCurrent.setTextColor(color)
            binding.fragmentRealtimeTvCurrentUnit.setTextColor(color)
            binding.fragmentRealtimeImgStatus.setImageResource(drawable)
        }
    }

    // ###################################  CHART   ###################################
    // ------- INITIALIZE CHART -------
    /**
     * initialize empty chart
     */
    private fun initChart() {
        xAxisFormatter = RealtimeXAxisFormatter()
        yAxisFormatter = RealtimeYAxisFormatter(unit, true)

        with(binding.fragmentRealtimeChartRt) {
            // add empty data
            data = LineData().apply {
                addDataSet(createSet())
            }

            setTouchEnabled(true) // allow scrolling in diagramm

            description.text = getString(R.string.fragment_realtime_chart_description) // description text
            description.textColor = resources.getColor(R.color.color_realtime_text, null)

            legend.textColor = resources.getColor(R.color.color_realtime_text, null)

            rendererXAxis
            // x-axis top
            xAxis.apply {
                setDrawAxisLine(true)
                textColor = resources.getColor(R.color.color_realtime_text, null)
                valueFormatter = xAxisFormatter
            }
            // y-axis left (only show axis line)
            axisLeft.apply {
                setDrawAxisLine(true)
                valueFormatter = RealtimeYAxisFormatter(unit, false)
            }
            // y-axis right
            axisRight.apply {
                setDrawAxisLine(true)
                textColor = resources.getColor(R.color.color_realtime_text, null)
                valueFormatter = yAxisFormatter
            }

            invalidate()
            chartInitialized = true
        }
        binding.fragmentRealtimeChartRt.setOnChartValueSelectedListener(this) // register listener
    }

    /**
     * creates new data set with given properties
     */
    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, getString(R.string.fragment_realtime_chart_label))
        with(set) {
            color = resources.getColor(R.color.color_default, null) // set color of chart line
            setCircleColor(resources.getColor(R.color.color_default_variant, null)) // set color of circle
            setDrawValues(false) // values shouldn't be drawn above every circle
            highLightColor = resources.getColor(R.color.color_realtime_highlight, null) // color of highlighting lines
            highlightLineWidth = 0.75f
        }
        return set
    }

    // ------- HANDLING CHART -------
    /**
     * inserts one meter data entry into chart
     */
    private fun insertToChart(meterData: MeterDataRTDto) {
        if (!chartInitialized) { // init chart (we need unit)
            unit = getAppropriateMeterDataValue(meterData.unit)
            initChart()
        }

        val data: LineData = binding.fragmentRealtimeChartRt.data
        if (data != null) {
            val set = data.getDataSetByIndex(0)
            // add new meter data value to chart (x: current seconds; y: meter data value)
            data.addEntry(Entry(set.entryCount.toFloat(), getAppropriateMeterDataValue(meterData.meterDataValues).toFloat()), 0)
            data.notifyDataChanged()

            // let the chart know it's data has changed
            binding.fragmentRealtimeChartRt.notifyDataSetChanged()

            // limit the number of visible entries
            binding.fragmentRealtimeChartRt.setVisibleXRangeMaximum(CHART_ENTRY_COUNT)

            // move to the latest entry
            binding.fragmentRealtimeChartRt.moveViewToX(data.entryCount.toFloat())
        }
    }

    /**
     * clears the whole chart
     */
    private fun clearChart() {
        chartInitialized = false
        binding.fragmentRealtimeChartRt.clear()
        binding.fragmentRealtimeTvHighlightValue.text = ""
    }

    /**
     * no highlighted value in chart
     */
    override fun onNothingSelected() {
        binding.fragmentRealtimeTvHighlightValue.text = ""
    }

    /**
     * highlighted value in chart
     */
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        e?.let {
            val output: String = "${yAxisFormatter.getAxisLabel(it.y, null)} (${xAxisFormatter.getAxisLabel(it.x, null)})"
            binding.fragmentRealtimeTvHighlightValue.text = output
        }

    }

}