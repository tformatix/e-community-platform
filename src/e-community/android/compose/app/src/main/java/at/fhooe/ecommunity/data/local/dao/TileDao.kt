package at.fhooe.ecommunity.data.local.dao

import androidx.room.*
import at.fhooe.ecommunity.data.local.entity.Tile
import kotlinx.coroutines.flow.Flow

/**
 * data access object for the tile table
 * @see Dao
 */
@Dao
interface TileDao {
    /**
     * @return all tiles (flow reacts on updates)
     * @see Query
     */
    @Query("select * from tile order by `order` asc")
    fun getTiles(): Flow<List<Tile>>

    /**
     * inserts a tile (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tile: Tile)

    /**
     * inserts a sequence of tiles (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tiles: Iterable<Tile>)

    /**
     * deletes a tile
     * @see Delete
     */
    @Delete
    fun delete(tile: Tile)

    /**
     * updates a tile
     * @see Update
     */
    @Update
    fun update(tile: Tile)

    /**
     * updates a sequence of tiles
     * @see Update
     */
    @Update
    fun update(tiles: Iterable<Tile>)

    /**
     * deletes all tiles
     * @see Query
     */
    @Query("delete from tile")
    fun deleteTileTable()
}