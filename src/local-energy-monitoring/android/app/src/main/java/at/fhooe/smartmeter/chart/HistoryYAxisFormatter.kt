package at.fhooe.smartmeter.chart

import at.fhooe.smartmeter.dto.MeterDataHistDto
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.ceil
import kotlin.math.round

class HistoryYAxisFormatter(meterDataHistDto: MeterDataHistDto, disable: Boolean) : ValueFormatter() {
    private val meterData = meterDataHistDto
    private val disable = disable

    override fun getBarLabel(barEntry: BarEntry?): String {
        return barEntry?.y.toString()
    }

    /**
     * return formatted timestamp
     * */
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        if (disable) {
            return ""
        }

        return if (value == ceil(value)) { // 100.0 --> 100 W
            "${value.toInt()} ${meterData.Unit.activeEnergyPlus}"
        } else { // 0.5555 -> 0.5 W
            "${round(value * 10) / 10} ${meterData.Unit.activeEnergyPlus}"
        }
        //return "$value ${meterData.Unit.activeEnergyPlus}"
    }
}