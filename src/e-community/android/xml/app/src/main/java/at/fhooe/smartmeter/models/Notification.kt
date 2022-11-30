package at.fhooe.smartmeter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey var notificationId: String,
    var timestamp: String,
    var description: String,
    var isRead: Boolean,
    var priority: Int,
    var isForTutorial: Boolean)