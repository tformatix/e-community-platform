package at.fhooe.ecommunity.util

import android.content.Context
import android.text.Html
import android.util.Log
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.local.models.StatusDto
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * format some value (meter data, html strings)
 * @param mContext used for translations
 */
class ECommunityFormatter(private val mContext: Context) {

    /**
     * converts a html resource string(formation) to a charSequence
     * @param id strings id
     * @return formatted string
     */
    fun convertHtmlToString(id: Int): CharSequence {
        return Html.fromHtml(mContext.getString(id), Html.FROM_HTML_MODE_LEGACY)
    }

    /**
     * format smart meter values
     * >= 1000 = 1kW
     * >= 100000 = 1mW
     * >= 100000000 = 1GW
     * @param value meter data value
     * @param isEnergy is energy value (not power)
     * @return formatted meter data value
     */
    fun formatSmartMeterValue(value: Int, isEnergy: Boolean = false): String {
        var firstNumber = 0
        val commaNumber: Double
        var unit = ""
        val energyUnit = if (isEnergy) Constants.HOUR_UNIT else ""

        if (value < Constants.KILOWATT) {
            return "$value ${Constants.WATT_UNIT}$energyUnit"
        } else if (value >= Constants.KILOWATT && value < Constants.MEGAWATT) {
            firstNumber += value / Constants.KILOWATT
            commaNumber = value.toDouble() % Constants.KILOWATT
            unit = Constants.KILOWATT_UNIT
        } else if (value >= Constants.MEGAWATT && value < Constants.GIGAWATT) {
            firstNumber += value / Constants.MEGAWATT
            commaNumber = value.toDouble() % Constants.MEGAWATT
            unit = Constants.MEGAWATT_UNIT
        } else {
            firstNumber += value / Constants.GIGAWATT
            commaNumber = value.toDouble() % Constants.GIGAWATT
            unit = Constants.GIGAWATT_UNIT
        }

        return "$firstNumber.${(commaNumber * 10).toString().substring(0, 2)} $unit$energyUnit"
    }

    /**
     * format the timestamp from meter_data to hh:mm:ss
     * @param timestamp timestamp of meter data
     * @return formatted timestamp
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

    /**
     * format connection string of local device
     * @param statusDto status of local device
     * @return formatted connection
     */
    fun getConnectionString(statusDto: StatusDto): String {
        var conString = ""

        statusDto.isWiredConnected.let {
            if (it == true) {
                conString += "| Wired |"
            }
        }

        statusDto.wifiSSID.let {
            conString += "| Wifi: $it |"
        }

        return conString
    }

    fun convertDateToUnixTimestamp(_date: String): Long {
        val date = LocalDateTime.parse(_date)
        val zoneId: ZoneId = ZoneId.systemDefault()
        return date.atZone(zoneId).toEpochSecond()
    }

    fun convertUnixTimestampToDate(_date: String): LocalDate {
        return Instant.ofEpochMilli(_date.toLong() * 1000L).atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    fun convertUnixTimestampToOffsetDateTime(_epochValue: Long, _isStart: Boolean): OffsetDateTime {
        val date = convertUnixTimestampToDate(_epochValue.toString())
        var datetime = ""

        val month = if (date.monthValue < 10) {
            "0${date.monthValue}"
        } else { date.monthValue }

        val day = if (date.dayOfMonth < 10) {
            "0${date.dayOfMonth}"
        } else { date.dayOfMonth }

        datetime = if (_isStart) {
            "${date.year}-${month}-${day}T00:00:00.000Z"
        } else {
            "${date.year}-${month}-${day}T23:00:00.000Z"
        }

        return OffsetDateTime
            .parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .atZoneSameInstant(ZoneId.systemDefault()) // move to device's time zone
            .toOffsetDateTime()
    }
}