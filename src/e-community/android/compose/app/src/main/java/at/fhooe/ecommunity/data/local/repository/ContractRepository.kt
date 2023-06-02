package at.fhooe.ecommunity.data.local.repository

import at.fhooe.ecommunity.data.local.dao.ContractDao
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import kotlinx.coroutines.flow.Flow

/**
 * main access point for the contract table
 * @param contractDao data access object
 */
class ContractRepository(private val contractDao: ContractDao) {
    /**
     * inserts a contract
     */
    fun insert(contract: ConsentContract) {
        contractDao.insert(contract)
    }

    /**
     * inserts a sequence of contracts
     */
    fun insert(contracts: Iterable<ConsentContract>) {
        contractDao.insert(contracts)
    }

    /**
     * updates a contract
     */
    fun update(contract: ConsentContract) {
        contractDao.update(contract)
    }

    /**
     * updates a sequence of contracts
     */
    fun update(contracts: Iterable<ConsentContract>) {
        contractDao.update(contracts)
    }

    /**
     * get all contracts
     */
    fun getConsentContracts(): Flow<List<ConsentContract>> {
        return contractDao.getContracts()
    }

    /**
     * get contract for contractId
     */
    fun getContract(_contractId: String): ConsentContract {
        return contractDao.getContract(_contractId)
    }
}