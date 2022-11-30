package at.fhooe.smartmeter.dto

import java.sql.Timestamp

data class HistoryDto (val AesKey: String, val FromTimestamp: String, val ToTimestamp: String, val TimeResolution: Int) {
}