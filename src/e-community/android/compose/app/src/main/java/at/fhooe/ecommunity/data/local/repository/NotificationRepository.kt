package at.fhooe.ecommunity.data.local.repository

import at.fhooe.ecommunity.data.local.dao.NotificationDao
import at.fhooe.ecommunity.data.local.entity.Notification
import kotlinx.coroutines.flow.Flow

/**
 * main access point for the notification table
 * @param notificationDao data access object
 */
class NotificationRepository(private val notificationDao: NotificationDao) {
    /**
     * inserts a notification
     */
    fun insert(notification: Notification) {
        notificationDao.insert(notification)
    }

    /**
     * inserts a sequence of notifications
     */
    fun insertBulk(notifications: Iterable<Notification>) {
        notificationDao.insertBulk(notifications)
    }

    /**
     * @return all notifications (flow reacts on updates)
     */
    fun getNotifications(): Flow<List<Notification>> {
        return notificationDao.getNotifications()
    }

    /**
     * @return all tutorial notifications (flow reacts on updates)
     */
    fun getTutorialNotifications(): Flow<List<Notification>> {
        return notificationDao.getTutorialNotifications()
    }

    /**
     * deletes all tutorial notifications
     */
    fun deleteTutorialNotifications() {
        return notificationDao.deleteTutorialNotifications()
    }

}