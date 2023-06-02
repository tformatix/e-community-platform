package at.fhooe.ecommunity.data.local.repository

import at.fhooe.ecommunity.data.local.dao.ContractDao
import at.fhooe.ecommunity.data.local.dao.MeterDataHistDao
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import at.fhooe.ecommunity.data.local.entity.MeterDataHistContract
import kotlinx.coroutines.flow.Flow

/**
 * main access point for the contract table
 * @param contractDao data access object
 */
class MeterDataHistContractRepository(private val meterDataHistDao: MeterDataHistDao) {
    /**
     * inserts a contract
     */
    fun insert(contract: MeterDataHistContract) {
        meterDataHistDao.insert(contract)
    }

    /**
     * inserts a sequence of contracts
     */
    fun insert(contracts: Iterable<MeterDataHistContract>) {
        meterDataHistDao.insert(contracts)
    }

    /**
     * updates a contract
     */
    fun update(contract: MeterDataHistContract) {
        meterDataHistDao.update(contract)
    }

    /**
     * updates a sequence of contracts
     */
    fun update(contracts: Iterable<MeterDataHistContract>) {
        meterDataHistDao.update(contracts)
    }

    /**
     * get all contracts
     */
    fun getAllMeterDataHistContracts(): Flow<List<MeterDataHistContract>> {
        return meterDataHistDao.getMeterDataHistContracts()
    }

    /**
     * get contract for contractId
     */
    fun getMeterDataHistDataForContract(_contractId: String): Flow<List<MeterDataHistContract>> {
        return meterDataHistDao.getMeterDataHistContracts(_contractId)
    }
}