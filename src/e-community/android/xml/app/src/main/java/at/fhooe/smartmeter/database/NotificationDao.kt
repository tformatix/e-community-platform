package at.fhooe.smartmeter.database

import androidx.lifecycle.LiveData
import androidx.room.*
import at.fhooe.smartmeter.models.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification where isForTutorial == 0")
    fun getNotifications(): Flow<List<Notification>>

    @Query("SELECT * FROM notification where isForTutorial == 1")
    fun getTutorialNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: Notification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulk(notifications: Iterable<Notification>)

    @Delete
    fun delete(notification: Notification)

    @Update
    fun update(notification: Notification)

    @Query("DELETE FROM notification")
    fun deleteNotificationTable()

    @Query("DELETE FROM notification where isForTutorial == 1")
    fun deleteTutorialNotifications()

    @Query("SELECT COUNT(*) FROM notification")
    fun getNotificationCount() : Int
}