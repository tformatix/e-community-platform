package at.fhooe.smartmeter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "smart_meter")
data class SmartMeter(
    @PrimaryKey var ID: String,
    var name: String,
    var description: String
)


