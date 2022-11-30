package at.fhooe.smartmeter.dto

import java.sql.Timestamp

data class HistorySetupDto (val latestTimestamp: String, val maxTimeResolution: Int, val initTimestamp: String) {}