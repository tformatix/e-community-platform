package at.fhooe.smartmeter.chart

import android.util.Log
import at.fhooe.smartmeter.Constants.TAG
import at.fhooe.smartmeter.dto.MeterDataHistDto
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter


class HistoryXAxisFormatter(meterDataHistDto: MeterDataHistDto) : ValueFormatter() {

    private val meterData = meterDataHistDto

    override fun getBarLabel(barEntry: BarEntry?): String {
        return barEntry?.y.toString()
    }

    /**
     * return formatted timestamp
     * */
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val meterDataDto = meterData.MeterDataValues.singleOrNull { x -> x.id ==  value.toInt()}
        if (meterDataDto != null) {
            return getFormattedTimestamp(meterDataDto.timestamp, false)
        }

        return value.toString()
    }

    fun getTopLabel(value: Float, axis: AxisBase?) : String {
        val meterDataDto = meterData.MeterDataValues.singleOrNull { x -> x.id ==  value.toInt()}
        if (meterDataDto != null) {
            return getFormattedTimestamp(meterDataDto.timestamp, true)
        }

        return value.toString()
    }

    private fun getFormattedTimestamp(format: String, top: Boolean) : String {
        val dateString = format.split('T')
        val date = dateString[0].split('-')
        val time = dateString[1].split(':')
        var formatted: String = ""

        if (!top) {
            if (date[2].toInt() < 10) {
                formatted = "0${date[2].toInt()}"
            }
            else {
                formatted = date[2].toInt().toString()
            }

            if (date[1].toInt() < 10) {
                formatted = "${formatted}.0${date[1].toInt()}"
            }
            else {
                formatted = "${formatted}.${date[1].toInt()}"
            }
            return formatted
        }

        if (time[0].toInt() < 10) {
            formatted = "$formatted 0${time[0].toInt()}"
        }
        else {
            formatted = "$formatted ${time[0].toInt()}"
        }

        if (time[1].toInt() < 10) {
            formatted = "${formatted}:0${time[1].toInt()}"
        }
        else {
            formatted = "${formatted}:${time[1].toInt()}"
        }

        return formatted
    }
}