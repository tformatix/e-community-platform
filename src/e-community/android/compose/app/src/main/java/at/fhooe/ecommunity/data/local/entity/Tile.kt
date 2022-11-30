package at.fhooe.ecommunity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * entity of a tile (table: tile)
 * @param tileId id of the tile
 * @param region household/eCommunity
 * @param action consumption/feed_in
 * @param value current meter data value
 * @param isVisible tile visibility (filter function)
 * @param isLargeTile defines tile layout
 * @param order order of the tiles (user can decide)
 * @param colorId id of the displayed color
 * @see Entity
 */
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



