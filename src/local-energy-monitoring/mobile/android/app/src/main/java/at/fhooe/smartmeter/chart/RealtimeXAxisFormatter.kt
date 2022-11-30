package at.fhooe.smartmeter.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.roundToLong

class RealtimeXAxisFormatter : ValueFormatter() {

    override fun getBarLabel(barEntry: BarEntry?): String {
        return barEntry?.y.toString()
    }

    /**
     * return formatted timestamp
     * */
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (value >= 60) // 65.0 --> 1 min 5 s
            return "${(value / 60).toInt()} min ${(value % 60).toInt()}s"
        else if (value == ceil(value)) { // 1.0 --> 1 s
            return "${value.toInt()} s"
        } else { // 0.5555 -> 0.5s
            return "${round(value * 10) / 10} s"
        }
    }
}