package at.fhooe.smartmeter.database

import at.fhooe.smartmeter.models.Tile
import kotlinx.coroutines.flow.Flow

class TileRepository(private val tileDao: TileDao) {

    fun insert(tile: Tile) {
        tileDao.insert(tile)
    }

    fun insert(tiles: Iterable<Tile>) {
        tileDao.insert(tiles)
    }

    fun update(tile: Tile) {
        tileDao.update(tile)
    }

    fun update(tiles: Iterable<Tile>) {
        tileDao.update(tiles)
    }

    fun getTiles(): Flow<List<Tile>> {
        return tileDao.getTiles()
    }
}