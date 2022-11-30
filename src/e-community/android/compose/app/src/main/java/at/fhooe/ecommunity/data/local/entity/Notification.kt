package at.fhooe.ecommunity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * entity of a notification (table: notification)
 * @param notificationId id of the notification
 * @param timestamp time when notification occurred
 * @param description description of the notification
 * @param isRead did the user read the notification
 * @param priority priority of the notification
 * @param isForTutorial is a tutorial notification
 * @see Entity
 */
@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey var notificationId: String,
    var timestamp: String,
    var description: String,
    var isRead: Boolean,
    var priority: Int,
    var isForTutorial: Boolean)