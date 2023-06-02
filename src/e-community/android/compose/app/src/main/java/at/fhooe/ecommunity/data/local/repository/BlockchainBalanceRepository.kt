package at.fhooe.ecommunity.data.local.repository

import at.fhooe.ecommunity.data.local.dao.BlockchainBalanceDao
import at.fhooe.ecommunity.data.local.dao.ContractDao
import at.fhooe.ecommunity.data.local.entity.BlockchainBalance
import at.fhooe.ecommunity.data.local.entity.ConsentContract
import kotlinx.coroutines.flow.Flow

/**
 * main access point for the contract table
 * @param blockchainBalanceDao data access object
 */
class BlockchainBalanceRepository(private val blockchainBalanceDao: BlockchainBalanceDao) {
    /**
     * inserts a contract
     */
    fun insert(balance: BlockchainBalance) {
        blockchainBalanceDao.insert(balance)
    }

    /**
     * get all contracts
     */
    fun getBalance(): Flow<BlockchainBalance> {
        return blockchainBalanceDao.getBalance()
    }
}