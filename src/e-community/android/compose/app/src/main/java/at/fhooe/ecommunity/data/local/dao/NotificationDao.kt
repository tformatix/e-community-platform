package at.fhooe.ecommunity.data.local.dao

import androidx.room.*
import at.fhooe.ecommunity.data.local.entity.Notification
import kotlinx.coroutines.flow.Flow

/**
 * data access object for the notification table
 * @see Dao
 */
@Dao
interface NotificationDao {
    /**
     * @return all notifications (flow reacts on updates)
     * @see Query
     */
    @Query("SELECT * FROM notification where isForTutorial == 0")
    fun getNotifications(): Flow<List<Notification>>

    /**
     * @return all tutorial notifications (flow reacts on updates)
     * @see Query
     */
    @Query("SELECT * FROM notification where isForTutorial == 1")
    fun getTutorialNotifications(): Flow<List<Notification>>

    /**
     * inserts a notification (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: Notification)

    /**
     * inserts a sequence of notifications (REPLACE duplicates)
     * @see Insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBulk(notifications: Iterable<Notification>)

    /**
     * deletes a notification
     * @see Delete
     */
    @Delete
    fun delete(notification: Notification)

    /**
     * updates a notification
     * @see Update
     */
    @Update
    fun update(notification: Notification)

    /**
     * deletes all notifications
     * @see Query
     */
    @Query("DELETE FROM notification")
    fun deleteNotificationTable()

    /**
     * deletes all tutorial notifications
     * @see Query
     */
    @Query("DELETE FROM notification where isForTutorial == 1")
    fun deleteTutorialNotifications()

    /**
     * @return number of notifications
     * @see Query
     */
    @Query("SELECT COUNT(*) FROM notification")
    fun getNotificationCount() : Int
}