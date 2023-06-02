package at.fhooe.ecommunity.data.local.dao

import androidx.room.*
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.data.local.entity.MeterDataHistContract
import kotlinx.coroutines.flow.Flow

/**
 * data access object for the meterDataHist table
 * @see Dao
 */
@Dao
interface MeterDataHistDao {
    /**
     * @return all meter_data_hist_contracts (flow reacts on updates)
     * @see Query
     */
    @Query("select * from meter_data_hist_contract")
    fun getMeterDataHistContracts(): Flow<List<MeterDataHistContract>>

    /**
     * @return contracts for the contractId
     * @see Query
     */
    @Query("select * from meter_data_hist_contract where contractId = :contractId")
    fun getMeterDataHistContracts(contractId: String): Flow<List<MeterDataHistContract>>

    /**
     * inserts a contract (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contract: MeterDataHistContract)

    /**
     * inserts a sequence of contracts (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contracts: Iterable<MeterDataHistContract>)

    /**
     * deletes a contract
     * @see Delete
     */
    @Delete
    fun delete(contract: MeterDataHistContract)

    /**
     * updates a contract
     * @see Update
     */
    @Update()
    fun update(contract: MeterDataHistContract)

    /**
     * updates a sequence of contracts
     * @see Update
     */
    @Update
    fun update(contract: Iterable<MeterDataHistContract>)

    /**
     * deletes all contracts
     * @see Query
     */
    @Query("delete from meter_data_hist_contract")
    fun deleteContractsTable()
}