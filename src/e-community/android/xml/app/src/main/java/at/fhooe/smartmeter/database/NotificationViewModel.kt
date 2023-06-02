package at.fhooe.smartmeter.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.fhooe.smartmeter.models.Notification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(notification: Notification) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(notification)
    }

    fun insertBulk(notifications: Iterable<Notification>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertBulk(notifications)
    }

    /**
     * returns a LivaData list of the notifications
     */
    fun getNotifications(): Flow<List<Notification>> {
        return repository.getNotifications()
    }

    /**
     * returns a LivaData list of the notifications for the tutorial
     */
    fun getTutorialNotifications(): Flow<List<Notification>> {
        return repository.getTutorialNotifications()
    }

    /**
     * delete the tutorial notifications (used for preview mode)
     */
    fun deleteTutorialNotifications() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteTutorialNotifications()
    }

}

class NotificationViewModelFactory(private val repository: NotificationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
