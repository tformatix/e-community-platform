package at.fhooe.smartmeter.context

import at.fhooe.smartmeter.dto.MeterDataHistDto

data class HistoryData(var meterDataHistDto: MeterDataHistDto, var fromTimestamp: String, var toTimestamp: String, var timeResolution: Int) {
}