package at.fhooe.smartmeter.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import at.fhooe.smartmeter.models.Member
import at.fhooe.smartmeter.models.Notification
import at.fhooe.smartmeter.models.Tile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val databaseName = "smart_meter_db"

@Database(entities = [Member::class, Notification::class, Tile::class], version = 1, exportSchema = false)
public abstract class SmartMeterDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun notificationDao(): NotificationDao
    abstract fun tileDao(): TileDao


    private class SmartMeterDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val memberDao = database.memberDao()
                    memberDao.deleteMemberTable()

                    val notificationDao = database.notificationDao()
                    notificationDao.deleteNotificationTable()

                    val tileDao = database.tileDao()
                    tileDao.deleteTileTable()
                }
            }
        }
    }


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SmartMeterDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SmartMeterDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        SmartMeterDatabase::class.java,
                            databaseName
                        )
                    .addCallback(SmartMeterDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}