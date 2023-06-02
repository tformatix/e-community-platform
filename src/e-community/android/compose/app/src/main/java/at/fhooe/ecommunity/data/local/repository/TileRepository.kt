package at.fhooe.ecommunity.data.local.repository

import at.fhooe.ecommunity.data.local.dao.TileDao
import at.fhooe.ecommunity.data.local.entity.Tile
import kotlinx.coroutines.flow.Flow

/**
 * main access point for the tile table
 * @param tileDao data access object
 */
class TileRepository(private val tileDao: TileDao) {
    /**
     * inserts a tile
     */
    fun insert(tile: Tile) {
        tileDao.insert(tile)
    }

    /**
     * inserts a sequence of tiles
     */
    fun insert(tiles: Iterable<Tile>) {
        tileDao.insert(tiles)
    }

    /**
     * updates a tile
     */
    fun update(tile: Tile) {
        tileDao.update(tile)
    }

    /**
     * updates a sequence of tiles
     */
    fun update(tiles: Iterable<Tile>) {
        tileDao.update(tiles)
    }

    fun getTiles(): Flow<List<Tile>> {
        return tileDao.getTiles()
    }
}