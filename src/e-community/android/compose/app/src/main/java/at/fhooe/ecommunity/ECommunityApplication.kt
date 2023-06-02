package at.fhooe.ecommunity

import android.app.Application
import at.fhooe.ecommunity.data.local.ApplicationDatabase
import at.fhooe.ecommunity.data.local.repository.*
import at.fhooe.ecommunity.data.remote.repository.CloudRESTRepository
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.data.remote.repository.LocalRESTRepository
import at.fhooe.ecommunity.data.remote.repository.RemoteExceptionRepository

const val TAG = "eCommunityLog" // logcat tag


/**
 * base class for maintaining global application state
 * @see Application
 */
class ECommunityApplication : Application() {
    // region room database
    private val applicationDatabase by lazy { ApplicationDatabase.getDatabase(this) }
    // endregion

    // region repositories - local
    val notificationRepository by lazy { NotificationRepository(applicationDatabase.notificationDao()) }
    val tileRepository by lazy { TileRepository(applicationDatabase.tileDao()) }
    val contractRepository by lazy { ContractRepository(applicationDatabase.contractDao()) }
    val meterDataHistContractRepository by lazy { MeterDataHistContractRepository(applicationDatabase.meterDataHistContract()) }
    val blockchainBalanceRepository by lazy { BlockchainBalanceRepository(applicationDatabase.blockchainBalanceDao()) }
    // endregion

    // region repositories - remote
    /**
     * remote exception handler
     */
    val remoteExceptionRepository by lazy { RemoteExceptionRepository(this) }

    /**
     * Cloud REST functionality
     */
    val cloudRESTRepository by lazy { CloudRESTRepository(this) }

    /**
     * Cloud SignalR functionality
     */
    val cloudSignalRRepository by lazy { CloudSignalRRepository() }

    /**
     * local (Raspberry) REST functionality
     */
    val localRESTRepository by lazy { LocalRESTRepository(this) }
    // endregion
}