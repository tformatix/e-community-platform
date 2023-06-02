package at.fhooe.ecommunity.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import at.fhooe.ecommunity.data.local.dao.*
import at.fhooe.ecommunity.data.local.entity.*

/**
 * name of room database
 */
const val databaseName = "application_db"

/**
 * database for whole application
 * @see Database
 * @see RoomDatabase
 */
@Database(entities = [Notification::class, Tile::class, ConsentContract::class, MeterDataHistContract::class, BlockchainBalance::class], version = 1, exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {
    /**
     * @return data access object for notification entries
     */
    abstract fun notificationDao(): NotificationDao

    /**
     * @return data access object for tile entries
     */
    abstract fun tileDao(): TileDao

    /**
     * @return data access object for contract entries
     */
    abstract fun contractDao(): ContractDao

    /**
     * @return data access object for meter data hist contracts
     */
    abstract fun meterDataHistContract(): MeterDataHistDao

    /**
     * @return data access object for the blockchain balance
     */
    abstract fun blockchainBalanceDao(): BlockchainBalanceDao

    companion object {
        /**
         * singleton prevents multiple instances of database opening at the same time
         * @see Volatile
         */
        @Volatile
        private var INSTANCE: ApplicationDatabase? = null

        /**
         * @param context current context
         * @return database object (singleton)
         */
        fun getDatabase(context: Context): ApplicationDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ApplicationDatabase::class.java,
                            databaseName
                        )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}