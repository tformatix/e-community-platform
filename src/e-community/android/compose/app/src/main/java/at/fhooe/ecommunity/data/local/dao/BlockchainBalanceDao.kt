package at.fhooe.ecommunity.data.local.dao

import androidx.room.*
import at.fhooe.ecommunity.data.local.entity.BlockchainBalance
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import kotlinx.coroutines.flow.Flow

/**
 * data access object for the blockchain balance table
 * @see Dao
 */
@Dao
interface BlockchainBalanceDao {
    /**
     * @return the balance
     * @see Query
     */
    @Query("select * from blockchain_balance")
    fun getBalance(): Flow<BlockchainBalance>

    /**
     * inserts a contract (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(balance: BlockchainBalance)

    /**
     * deletes a contract
     * @see Delete
     */
    @Delete
    fun delete(balance: BlockchainBalance)
}