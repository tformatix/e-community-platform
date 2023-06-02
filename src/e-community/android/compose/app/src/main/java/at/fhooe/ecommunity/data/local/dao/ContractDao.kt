package at.fhooe.ecommunity.data.local.dao

import androidx.room.*
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import kotlinx.coroutines.flow.Flow

/**
 * data access object for the contracts table
 * @see Dao
 */
@Dao
interface ContractDao {
    /**
     * @return all contracts (flow reacts on updates)
     * @see Query
     */
    @Query("select * from contract")
    fun getContracts(): Flow<List<ConsentContract>>

    /**
     * @return contract for the contractId
     * @see Query
     */
    @Query("select * from contract where contractId = :contractId")
    fun getContract(contractId: String): ConsentContract

    /**
     * inserts a contract (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contract: ConsentContract)

    /**
     * inserts a sequence of contracts (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contracts: Iterable<ConsentContract>)

    /**
     * deletes a contract
     * @see Delete
     */
    @Delete
    fun delete(contract: ConsentContract)

    /**
     * updates a contract
     * @see Update
     */
    @Update()
    fun update(contract: ConsentContract)

    /**
     * updates a sequence of contracts
     * @see Update
     */
    @Update
    fun update(contract: Iterable<ConsentContract>)

    /**
     * deletes all contracts
     * @see Query
     */
    @Query("delete from contract")
    fun deleteContractsTable()
}