package at.fhooe.smartmeter.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import at.fhooe.smartmeter.Constants
import at.fhooe.smartmeter.Constants.TAG
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.api.VolleyRequest
import at.fhooe.smartmeter.api.VolleyRequestQueue
import at.fhooe.smartmeter.chart.DoubleXLabelAxisRenderer
import at.fhooe.smartmeter.chart.HistoryXAxisFormatter
import at.fhooe.smartmeter.chart.HistoryYAxisFormatter
import at.fhooe.smartmeter.context.ContextHistory
import at.fhooe.smartmeter.context.HistoryData
import at.fhooe.smartmeter.databinding.FragmentHistoryBinding
import at.fhooe.smartmeter.dto.HistoryDto
import at.fhooe.smartmeter.dto.HistorySetupDto
import at.fhooe.smartmeter.dto.MeterDataDto
import at.fhooe.smartmeter.dto.MeterDataHistDto
import at.fhooe.smartmeter.models.TimeResolution
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.sql.Timestamp
import java.time.Period
import java.util.*
import kotlin.collections.ArrayList

const val TAG_HISTORY: String = "HistoryFragment"

class HistoryFragment : Fragment(), View.OnClickListener, VolleyRequest<HistoryDto>, SeekBar.OnSeekBarChangeListener {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var historySetup: HistorySetupDto
    private lateinit var meterDataHist: MeterDataHistDto
    private lateinit var historyTimeResolution: List<TimeResolution>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPrefs = activity?.getSharedPreferences(getString(R.string.shared_prefs_settings), Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        historyTimeResolution = fillTimeResolution()

        // check contextHistory - show data again when user selected another fragment
        val historyData : HistoryData? = ContextHistory.getData()

        initViewComponents(historyData)
        if (historyData != null) {
            initChart(historyData.meterDataHistDto)
        }
        else {
            sendVolleyRequest(Constants.REQUEST_TYPE_HISTORY_SETUP, null)
            binding.fragmentHistoryBarChart.setNoDataText(getString(R.string.fragment_history_chart_waiting))
            binding.fragmentHistoryBarChart.setNoDataTextColor(resources.getColor(R.color.color_default, null))
        }

        return binding.root
    }

    /**
     * save date, time, slider and chart values in a singleton
     * */
    override fun onDestroy() {
        val toTimestamp = "${binding.fragmentHistoryToDate.text};${binding.fragmentHistoryToTime.text}"
        val fromTimestamp = "${binding.fragmentHistoryFromDate.text};${binding.fragmentHistoryFromTime.text}"
        if (this::meterDataHist.isInitialized) {
            ContextHistory.setData(HistoryData(meterDataHist, fromTimestamp, toTimestamp, binding.fragmentHistorySeekbarTime.progress-1))
        }

        super.onDestroy()
    }

    /**
     * for TextEdits open Pickers (Date or Time)
     * for Button send Request and refresh chart
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.fragmentHistoryButtonRefresh.id -> {
                /* send History Request */
                val fromTimestamp = parseTimestamp(binding.fragmentHistoryFromDate.text.toString(), binding.fragmentHistoryFromTime.text.toString())
                val toTimestamp = parseTimestamp(binding.fragmentHistoryToDate.text.toString(), binding.fragmentHistoryToTime.text.toString())
                val key = sharedPrefs.getString(getString(R.string.shared_prefs_settings_aeskey), "x")?.replace(" ", "")

                val historyDto = HistoryDto(key!!, fromTimestamp, toTimestamp, historyTimeResolution[binding.fragmentHistorySeekbarTime.progress-1].minutes)
                sendVolleyRequest(Constants.REQUEST_TYPE_HISTORY, historyDto)
            }
            binding.fragmentHistoryFromDate.id -> {
                selectDate(binding.fragmentHistoryFromDate)
            }
            binding.fragmentHistoryFromTime.id -> {
                selectTime(binding.fragmentHistoryFromTime)
            }
            binding.fragmentHistoryToDate.id -> {
                selectDate(binding.fragmentHistoryToDate)
            }
            binding.fragmentHistoryToTime.id -> {
                selectTime(binding.fragmentHistoryToTime)
            }
            else -> {

            }
        }
    }

    /**
     * initialize views and set Listeners
     */
    private fun initViewComponents(historyData: HistoryData?) {
        val calendarNow: Calendar = Calendar.getInstance(Locale.GERMANY)

        if (historyData != null) {
            val toDate = historyData.toTimestamp.split(';')[0]
            val toTime = historyData.toTimestamp.split(';')[1]
            val fromDate = historyData.fromTimestamp.split(';')[0]
            val fromTime = historyData.fromTimestamp.split(';')[1]

            binding.fragmentHistoryToDate.text = toDate
            binding.fragmentHistoryToTime.text = toTime
            binding.fragmentHistoryFromDate.text = fromDate
            binding.fragmentHistoryFromTime.text = fromTime
            binding.fragmentHistorySeekbarTime.progress = historyData.timeResolution
        }
        else {
            binding.fragmentHistoryToDate.text = formatCalendar(calendarNow, true)
            binding.fragmentHistoryToTime.text = formatCalendar(calendarNow, false)
        }

        binding.fragmentHistoryButtonRefresh.setOnClickListener(this)
        binding.fragmentHistoryFromDate.setOnClickListener(this)
        binding.fragmentHistoryFromTime.setOnClickListener(this)
        binding.fragmentHistoryToDate.setOnClickListener(this)
        binding.fragmentHistoryToTime.setOnClickListener(this)

        binding.fragmentHistorySeekbarTime.setOnSeekBarChangeListener(this)
        binding.fragmentHistorySeekbarTime.min = 1
        binding.fragmentHistorySeekbarTime.max = historyTimeResolution.size
        binding.fragmentHistorySeekbarTime.incrementProgressBy(1)
    }

    /**
     * show a datePickerDialog when a date got clicked
     * historySetup.latestTimestamp is the minium date
     */
    private fun selectDate(saveDateTextView: TextView) {
        val splittedDate = saveDateTextView.text.split('.')
        val datePickerDialog = DatePickerDialog(requireContext(), { view, year, month, dayOfMonth ->
            run {
                val calendar: Calendar = Calendar.getInstance()
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                saveDateTextView.text = formatCalendar(calendar, true)
                validateTimes()
            }
        }, splittedDate[2].toInt(), splittedDate[1].toInt(), splittedDate[0].toInt())

        val datePicker = datePickerDialog.datePicker

        // if historySetup Request failed, historySetup is not initialized
        if (this::historySetup.isInitialized) {
            val minDate = historySetup.latestTimestamp.split('T')
            datePicker.minDate = Timestamp.valueOf("${minDate[0]} ${minDate[1]}").time
        }

        datePicker.maxDate = Calendar.getInstance().timeInMillis
        datePicker.updateDate(splittedDate[2].toInt(), splittedDate[1].toInt() - 1, splittedDate[0].toInt())

        datePickerDialog.show()
    }

    /**
     * show a timePickerDialog when a time got clicked
     */
    private fun selectTime(saveTimeTextView: TextView) {
        val splittedTime: List<String> = saveTimeTextView.text.split(':')
        val timePickerDialog = TimePickerDialog(requireContext(), { view, hour, min ->
            run {
                val calendar: Calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, min)

                saveTimeTextView.text = formatCalendar(calendar, false)
                validateTimes()
            }
        }, splittedTime[0].toInt(), splittedTime[1].toInt(), true)

        timePickerDialog.show()
    }

    /**
     * parse two textEdits (Date & Time) to an valid Timestamp
     * format: yyyy-mm-dd hh:mm:ss
     */
    private fun parseTimestamp(date: String, time: String): String {
        val splittedDate: List<String> = date.split('.')
        return "${splittedDate[2]}-${splittedDate[1]}-${splittedDate[0]}T$time:00"
    }

    /**
     * format Calendar Objects
     * date indicates: date or time string
     * --> for example: 12.05.2012 12:09
     */
    private fun formatCalendar(calendar: Calendar, date: Boolean) : String {
        var formatted: String = ""

        if (date) {
            // add 1 to month because january is 0
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DATE)

            if (day < 10) {
                formatted = "0${day}"
            }
            else {
                formatted = day.toString()
            }

            if (month < 10) {
                formatted = "${formatted}.0${month}"
            }
            else {
                formatted = "${formatted}.${month}"
            }

            formatted = "${formatted}.${calendar.get(Calendar.YEAR)}"
        }
        else {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val min = calendar.get(Calendar.MINUTE)

            if (hour < 10) {
                formatted = "0${hour}"
            }
            else {
                formatted = hour.toString()
            }

            if (min < 10) {
                formatted = "${formatted}:0${min}"
            }
            else {
                formatted = "${formatted}:${min}"
            }
        }

        return formatted
    }

    /**
     * method for sending http requests to c# api
     * */
    override fun sendVolleyRequest(requestType: Int, requestObject: HistoryDto?) {
        val ip = sharedPrefs.getString(getString(R.string.shared_prefs_settings_ip), "")
        val key = sharedPrefs.getString(getString(R.string.shared_prefs_settings_aeskey), "")?.replace(" ", "")

        if (ip != "") {
            val url: String
            var requestMethod: Int = Request.Method.GET
            val jsonRequest: JSONObject?

            when (requestType) {
                Constants.REQUEST_TYPE_HISTORY_SETUP -> {
                    url = resources.getString(R.string.backend_url_history_setup, ip, key)
                    jsonRequest = null
                }
                Constants.REQUEST_TYPE_HISTORY -> {
                    url = resources.getString(R.string.backend_url_history, ip)
                    jsonRequest = JSONObject(Gson().toJson(requestObject))
                    requestMethod = Request.Method.POST
                }
                else -> {
                    url = ""
                    jsonRequest = null
                }
            }

            val volleyRequest = JsonObjectRequest(requestMethod, url, jsonRequest,
                    { response ->
                        Log.d(TAG_HISTORY, "Response: %s".format(response.toString()))

                        when (requestType) {
                            Constants.REQUEST_TYPE_HISTORY_SETUP -> {

                                historySetup = Gson().fromJson(response.toString(), HistorySetupDto::class.java)
                                val dateString = historySetup.initTimestamp.split('T')
                                val date = dateString[0].split('-')
                                val time = dateString[1].split(':')

                                val calendar: Calendar = Calendar.getInstance()
                                calendar.set(Calendar.YEAR, date[0].toInt())
                                calendar.set(Calendar.MONTH, date[1].toInt() - 1)
                                calendar.set(Calendar.DAY_OF_MONTH, date[2].toInt())

                                calendar.set(Calendar.HOUR_OF_DAY, time[0].toInt())
                                calendar.set(Calendar.MINUTE, time[1].toInt())
                                calendar.set(Calendar.SECOND, time[2].toInt())

                                binding.fragmentHistoryFromDate.text = formatCalendar(calendar, true)
                                binding.fragmentHistoryFromTime.text = formatCalendar(calendar, false)

                                setMaxTimeResolution(historySetup.maxTimeResolution)
                                loadDefaultHistoryData()
                            }
                            Constants.REQUEST_TYPE_HISTORY -> {

                                val itemType = object : TypeToken<ArrayList<MeterDataDto<Double>>>() {}.type
                                val meterDataHistList = Gson().fromJson<ArrayList<MeterDataDto<Double>>>(response.getJSONArray("meterDataValues").toString(), itemType)

                                // Parse Json Response to MeterDataDto<T>
                                val meterDataHistUnitType = object : TypeToken<MeterDataDto<String>>() {}.type
                                val meterDataHistDoubleType = object : TypeToken<MeterDataDto<Double>>() {}.type

                                val meterDataHistUnit = Gson().fromJson<MeterDataDto<String>>(response.getString("unit"), meterDataHistUnitType)
                                val meterDataHistMin = Gson().fromJson<MeterDataDto<Double>>(response.getString("min"), meterDataHistDoubleType)
                                val meterDataHistAvg = Gson().fromJson<MeterDataDto<Double>>(response.getString("avg"), meterDataHistDoubleType)
                                val meterDataHistMax = Gson().fromJson<MeterDataDto<Double>>(response.getString("max"), meterDataHistDoubleType)

                                val meterDataHist = MeterDataHistDto(meterDataHistUnit, meterDataHistMin, meterDataHistAvg, meterDataHistMax, meterDataHistList)

                                // fill chart with data
                                initChart(meterDataHist)
                            }
                            else -> {

                            }
                        }

                    },
                    { error ->
                        Log.e(TAG_HISTORY, "Error Response: %s".format(error.toString()))

                        Snackbar.make(binding.fragmentHistoryBarChart, getString(R.string.volley_backend_error), Snackbar.LENGTH_LONG)
                                .setAction(R.string.volley_backend_resend) {
                                    when (requestType) {
                                        Constants.REQUEST_TYPE_HISTORY_SETUP -> {
                                            sendVolleyRequest(Constants.REQUEST_TYPE_HISTORY_SETUP, null)
                                        }
                                    }
                                }
                                .show()
                    }
            )

            Log.d(TAG_HISTORY, "sending Request... $jsonRequest")
            VolleyRequestQueue.getInstance(requireContext()).addToRequestQueue(volleyRequest)
        }
        else {
            Toast.makeText(activity, getString(R.string.shared_prefs_settings_missing_ip), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * initialize bar chart and fill it
     * */
    private fun initChart(meterDataHistDto: MeterDataHistDto) {
        GlobalScope.launch { fillChart(meterDataHistDto) }

        try {
            // LimitLine (Thresholds)
            val limitMin = LimitLine(meterDataHistDto.Min.activeEnergyPlus.toFloat())
            val limitAvg = LimitLine(meterDataHistDto.Avg.activeEnergyPlus.toFloat())
            val limitMax = LimitLine(meterDataHistDto.Max.activeEnergyPlus.toFloat())

            limitMin.lineColor = Color.YELLOW
            limitAvg.lineColor = Color.GREEN
            limitMax.lineColor = Color.RED

            // Legend
            val legend = binding.fragmentHistoryBarChart.legend
            legend.formSize = 10f
            legend.form = Legend.LegendForm.SQUARE
            legend.textColor = Color.BLACK
            legend.xEntrySpace = 5f
            legend.yEntrySpace = 5f
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.yOffset = 13f
            legend.setDrawInside(false)

            val legendEntryMin = LegendEntry()
            legendEntryMin.label = "Min: ${meterDataHistDto.Min.activeEnergyPlus.toFloat()} ${meterDataHistDto.Unit.activeEnergyPlus}"
            legendEntryMin.formColor = Color.YELLOW

            val legendEntryAvg = LegendEntry()
            legendEntryAvg.label = "Avg: ${meterDataHistDto.Avg.activeEnergyPlus.toFloat()} ${meterDataHistDto.Unit.activeEnergyPlus}"
            legendEntryAvg.formColor = Color.GREEN

            val legendEntryMax = LegendEntry()
            legendEntryMax.label = "Max: ${meterDataHistDto.Max.activeEnergyPlus.toFloat()} ${meterDataHistDto.Unit.activeEnergyPlus}"
            legendEntryMax.formColor = Color.RED

            // Axis
            val xAxisFormatter = HistoryXAxisFormatter(meterDataHistDto)
            val yRightAxisFormatter = HistoryYAxisFormatter(meterDataHistDto, true)
            val yLeftAxisFormatter = HistoryYAxisFormatter(meterDataHistDto, false)

            val xTAxis: XAxis = binding.fragmentHistoryBarChart.xAxis
            xTAxis.position = XAxisPosition.BOTH_SIDED
            xTAxis.textSize = 10f
            xTAxis.setDrawAxisLine(true)
            xTAxis.setDrawGridLines(false)
            xTAxis.valueFormatter = xAxisFormatter

            val yLAxis: YAxis = binding.fragmentHistoryBarChart.axisLeft
            yLAxis.textSize = 10f
            yLAxis.setDrawAxisLine(true)
            yLAxis.setDrawGridLines(false)
            yLAxis.valueFormatter = yLeftAxisFormatter

            yLAxis.removeAllLimitLines()
            yLAxis.addLimitLine(limitMin)
            yLAxis.addLimitLine(limitAvg)
            yLAxis.addLimitLine(limitMax)

            val yRAxis: YAxis = binding.fragmentHistoryBarChart.axisRight
            yRAxis.textSize = 10f
            yRAxis.setDrawAxisLine(true)
            yRAxis.setDrawGridLines(false)
            yRAxis.valueFormatter = yRightAxisFormatter

            // set textColor to White if darkMode is active
            if (isDarkModeActive()) {
                yRAxis.textColor = Color.WHITE
                yLAxis.textColor = Color.WHITE
                xTAxis.textColor = Color.WHITE
                legend.textColor = Color.WHITE
            }

            val entries: MutableList<LegendEntry> = ArrayList()
            entries.add(legendEntryMin)
            entries.add(legendEntryAvg)
            entries.add(legendEntryMax)

            legend.setCustom(entries)

            // manually set description disabled..
            val description = Description()
            description.isEnabled = false

            binding.fragmentHistoryBarChart.setXAxisRenderer(DoubleXLabelAxisRenderer(binding.fragmentHistoryBarChart.viewPortHandler, binding.fragmentHistoryBarChart.xAxis, binding.fragmentHistoryBarChart.getTransformer(YAxis.AxisDependency.LEFT),HistoryXAxisFormatter(meterDataHistDto)))
            binding.fragmentHistoryBarChart.description = description
            binding.fragmentHistoryBarChart.invalidate()

            meterDataHist = meterDataHistDto
        }
        catch (e: Exception) {
            Log.e(TAG, "HistoryFragment::initChart(): chart library error")
        }
    }

    /**
     * fill chart with values using coroutines
     * */
    private suspend fun fillChart(meterDataHistDto: MeterDataHistDto) {
        withContext(Dispatchers.IO) {
            try {
                binding.fragmentHistoryBarChart.clear()

                val entries: MutableList<BarEntry> = ArrayList()

                for (item in meterDataHistDto.MeterDataValues) {
                    entries.add(BarEntry(item.id.toFloat(), item.activeEnergyPlus.toFloat()))
                }

                val lineDataSet = BarDataSet(entries, "History")
                val lineData = BarData(lineDataSet)

                if (isDarkModeActive()) {
                    lineData.setValueTextColor(Color.WHITE)
                }

                binding.fragmentHistoryBarChart.data = lineData
                binding.fragmentHistoryBarChart.invalidate()
            }
            catch (e: Exception) {
                Log.e(TAG, "HistoryFragment::fillchart(): chart library error")
            }
        }
    }

    /**
     * checks if the device has dark mode enabled
     * */
    private fun isDarkModeActive() : Boolean {
        return when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    /**
     * fills possible timeResolutions
     * */
    private fun fillTimeResolution() : List<TimeResolution> {
        return listOf(
                TimeResolution(15, "15min"),
                TimeResolution(30, "30min"),
                TimeResolution(60, "1h"),
                TimeResolution(120, "2h"),
                TimeResolution(300, "5h"),
                TimeResolution(720, "12h"),
                TimeResolution(1440, "1T"),
                TimeResolution(10080, "1W"),
                TimeResolution(43800, "1M"),
                TimeResolution(525600, "1Y")
        )
    }

    /**
     * sets maximum time resolution to slider
     * */
    private fun setMaxTimeResolution(maxTimeResolution: Int) {
        val maxIndex = historyTimeResolution.filter { x -> x.minutes <= maxTimeResolution }.size

        binding.fragmentHistorySeekbarTime.max = maxIndex
    }

    /**
     * loads the default history data
     */
    private fun loadDefaultHistoryData() {
        val fromTimestamp = parseTimestamp(binding.fragmentHistoryFromDate.text.toString(), binding.fragmentHistoryFromTime.text.toString())
        val toTimestamp = parseTimestamp(binding.fragmentHistoryToDate.text.toString(), binding.fragmentHistoryToTime.text.toString())
        val key = sharedPrefs.getString(getString(R.string.shared_prefs_settings_aeskey), "x")?.replace(" ", "")

        binding.fragmentHistorySeekbarTime.progress = binding.fragmentHistorySeekbarTime.max
        val historyDto = HistoryDto(key!!, fromTimestamp, toTimestamp, historyTimeResolution[binding.fragmentHistorySeekbarTime.max-1].minutes)
        sendVolleyRequest(Constants.REQUEST_TYPE_HISTORY, historyDto)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        binding.fragmentHistorySeekbarTimeValue.text = historyTimeResolution[progress-1].display
    }

    /**
     * check if times are correct. fromTimestamp has to be older than toTimestamp
     * if correct.. set maxTimeResolution
     * if not correct.. disable refresh button
     * */
    private fun validateTimes() {
        val fromDate = binding.fragmentHistoryFromDate.text
        val fromTime = binding.fragmentHistoryFromTime.text
        val toDate = binding.fragmentHistoryToDate.text
        val toTime = binding.fragmentHistoryToTime.text

        val calendarFrom = Calendar.getInstance()
        calendarFrom.set(Calendar.YEAR, fromDate.split('.')[2].toInt())
        calendarFrom.set(Calendar.MONTH, fromDate.split('.')[1].toInt() - 1)
        calendarFrom.set(Calendar.DAY_OF_MONTH, fromDate.split('.')[0].toInt())
        calendarFrom.set(Calendar.HOUR_OF_DAY, fromTime.split(':')[0].toInt())
        calendarFrom.set(Calendar.MINUTE, fromTime.split(':')[1].toInt())

        val calendarTo = Calendar.getInstance()
        calendarTo.set(Calendar.YEAR, toDate.split('.')[2].toInt())
        calendarTo.set(Calendar.MONTH, toDate.split('.')[1].toInt() - 1)
        calendarTo.set(Calendar.DAY_OF_MONTH, toDate.split('.')[0].toInt())
        calendarTo.set(Calendar.HOUR_OF_DAY, toTime.split(':')[0].toInt())
        calendarTo.set(Calendar.MINUTE, toTime.split(':')[1].toInt())

        val diffInMinutes = (calendarTo.timeInMillis - calendarFrom.timeInMillis) / 1000 / 60

        // difference has to be greater than 15 minutes (sample rate)
        if (diffInMinutes < 15) {
            binding.fragmentHistoryButtonRefresh.isEnabled = false
            Toast.makeText(activity, R.string.fragment_history_invalid_times, Toast.LENGTH_SHORT).show()
        }
        else {
            binding.fragmentHistoryButtonRefresh.isEnabled = true
            setMaxTimeResolution(diffInMinutes.toInt())
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeekBar?) { }
}