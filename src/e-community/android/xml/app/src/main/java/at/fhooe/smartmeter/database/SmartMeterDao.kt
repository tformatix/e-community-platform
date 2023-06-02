package at.fhooe.smartmeter.database

import androidx.room.*
import at.fhooe.smartmeter.models.SmartMeter
import kotlinx.coroutines.flow.Flow

@Dao
interface SmartMeterDao {
    @Query("select * from smart_meter order by `name` asc")
    fun getSmartMeters(): Flow<List<SmartMeter>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(smartMeter: SmartMeter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(smartMeter: Iterable<SmartMeter>)

    @Delete
    fun delete(smartMeter: SmartMeter)

    @Update
    fun update(smartMeter: SmartMeter)

    @Update
    fun update(smartMeter: Iterable<SmartMeter>)

    @Query("delete from smart_meter")
    fun deleteTileTable()
}