package at.fhooe.smartmeter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tile")
data class Tile(
    @PrimaryKey
    var tileId: String,
    var region: String,
    var action: String,
    var value: String,
    var isVisible: Boolean,
    var isLargeTile: Boolean = false,
    var order: Int,
    var colorId: Int) {}



