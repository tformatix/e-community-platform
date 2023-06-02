package at.fhooe.smartmeter.database

import androidx.room.*
import at.fhooe.smartmeter.models.Tile
import kotlinx.coroutines.flow.Flow

@Dao
interface TileDao {
    @Query("select * from tile order by `order` asc")
    fun getTiles(): Flow<List<Tile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tile: Tile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tiles: Iterable<Tile>)

    @Delete
    fun delete(tile: Tile)

    @Update
    fun update(tile: Tile)

    @Update
    fun update(tiles: Iterable<Tile>)

    @Query("delete from tile")
    fun deleteTileTable()
}