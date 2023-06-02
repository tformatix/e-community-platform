package at.fhooe.smartmeter.database

import at.fhooe.smartmeter.models.SmartMeter
import kotlinx.coroutines.flow.Flow

class SmartMeterRepository (private val smartMeterDao: SmartMeterDao) {

    fun insert(smartMeter: SmartMeter) {
        smartMeterDao.insert(smartMeter)
    }

    fun insert(smartMeters: Iterable<SmartMeter>) {
        smartMeterDao.insert(smartMeters)
    }

    fun update(smartMeter: SmartMeter) {
        smartMeterDao.update(smartMeter)
    }

    fun update(smartMeter: Iterable<SmartMeter>) {
        smartMeterDao.update(smartMeter)
    }

    fun getSmartMeters(): Flow<List<SmartMeter>> {
        return smartMeterDao.getSmartMeters()
    }
}