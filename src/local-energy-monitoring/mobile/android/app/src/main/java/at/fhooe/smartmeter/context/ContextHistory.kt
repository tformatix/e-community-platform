package at.fhooe.smartmeter.context

import at.fhooe.smartmeter.dto.MeterDataHistDto
import java.sql.Timestamp
import kotlin.properties.Delegates

object ContextHistory {
    private lateinit var historyData: HistoryData

    init { }

    fun setData(historyData: HistoryData) {
        this.historyData = historyData
    }

    fun getData() : HistoryData? {
        if (this::historyData.isInitialized) {
            return historyData
        }

        return null
    }

}