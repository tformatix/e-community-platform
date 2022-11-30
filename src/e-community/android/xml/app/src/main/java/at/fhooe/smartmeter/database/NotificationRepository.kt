package at.fhooe.smartmeter.database

import androidx.lifecycle.LiveData
import at.fhooe.smartmeter.models.Notification
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    fun insert(notification: Notification) {
        notificationDao.insert(notification)
    }

    fun insertBulk(notifications: Iterable<Notification>) {
        notificationDao.insertBulk(notifications)
    }

    fun getNotifications(): Flow<List<Notification>> {
        return notificationDao.getNotifications()
    }

    fun getTutorialNotifications(): Flow<List<Notification>> {
        return notificationDao.getTutorialNotifications()
    }

    fun deleteTutorialNotifications() {
        return notificationDao.deleteTutorialNotifications()
    }

}