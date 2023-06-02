package at.fhooe.smartmeter.util

import android.content.Context
import android.text.Html
import android.util.Log
import at.fhooe.smartmeter.TAG
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class TextFormatter(context: Context) {

    private val mContext = context

    /**
     * converts a html resource string(formation) to a charSequence
     */
    fun convertHtmlToString(id: Int): CharSequence {
        return Html.fromHtml(mContext.getString(id),Html.FROM_HTML_MODE_LEGACY)
    }

    /**
     * format smart meter values
     * >= 1000 = 1kW
     * >= 100000 = 1mW
     * >= 100000000 = 1GW
     */
    fun formatSmartMeterValue(value: Int): String {
        var formattedValue = ""
        var firstNumber = 0
        var commaNumber = 0.0
        var unit = ""

        if (value < Constants.KILOWATT) {
            return "$value ${Constants.WATT_UNIT}"
        }
        else if (value >= Constants.KILOWATT && value < Constants.MEGAWATT) {
            firstNumber += value / Constants.KILOWATT
            commaNumber = value.toDouble() % Constants.KILOWATT
            unit = Constants.KILOWATT_UNIT
        }
        else if (value >= Constants.MEGAWATT && value < Constants.GIGAWATT) {
            firstNumber += value / Constants.MEGAWATT
            commaNumber = value.toDouble() % Constants.MEGAWATT
            unit = Constants.MEGAWATT_UNIT
        }
        else {
            firstNumber += value / Constants.GIGAWATT
            commaNumber = value.toDouble() % Constants.GIGAWATT
            unit = Constants.GIGAWATT_UNIT
        }

        return "$firstNumber.${commaNumber.toString().substring(0,2)} $unit"
    }

    /**
     * format the timestamp from meter_data to hh:mm:ss
     */
    fun formatTimestamp(timestamp: String): String {
        var formatTime = ""
        val localDateTime = OffsetDateTime
            .parse("${timestamp}", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZoneSameInstant(ZoneId.systemDefault()) // move to device's time zone
            .toOffsetDateTime()

        formatTime += if (localDateTime.hour < 10) {
            "0${localDateTime.hour}:"
        } else {
            "${localDateTime.hour}:"
        }

        formatTime += if (localDateTime.minute < 10) {
            "0${localDateTime.minute}:"
        } else {
            "${localDateTime.minute}:"
        }

        formatTime += if (localDateTime.second < 10) {
            "0${localDateTime.second}"
        } else {
            "${localDateTime.second}"
        }

        return formatTime
    }
}