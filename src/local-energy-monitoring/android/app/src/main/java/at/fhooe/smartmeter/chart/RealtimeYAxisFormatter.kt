package at.fhooe.smartmeter.chart

import at.fhooe.smartmeter.dto.MeterDataHistDto
import at.fhooe.smartmeter.dto.MeterDataRTDto
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.ceil
import kotlin.math.round

class RealtimeYAxisFormatter(private val unit: String, private val enabled: Boolean) : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        return barEntry?.y.toString()
    }

    /**
     * return formatted timestamp
     * */
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if(!enabled)
            return ""
        return if (value == ceil(value)) { // 100.0 --> 100 W
            "${value.toInt()} $unit"
        } else { // 0.5555 -> 0.5 W
            "${round(value * 10) / 10} $unit"
        }
    }
}