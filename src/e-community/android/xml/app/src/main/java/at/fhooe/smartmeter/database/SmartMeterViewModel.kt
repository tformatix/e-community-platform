package at.fhooe.smartmeter.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.fhooe.smartmeter.models.SmartMeter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SmartMeterViewModel(private val repository: SmartMeterRepository) : ViewModel() {

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(smartMeter: SmartMeter) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(smartMeter)
    }

    fun insert(smartMeters: Iterable<SmartMeter>) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(smartMeters)
    }

    fun update(smartMeter: SmartMeter) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(smartMeter)
    }

    fun update(smartMeters: Iterable<SmartMeter>) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(smartMeters)
    }

    /**
     * returns a LivaData list of the SmartMeter
     */
    fun getSmartMeters(): Flow<List<SmartMeter>> {
        return repository.getSmartMeters()
    }

}

class SmartMeterViewModelFactory(private val repository: SmartMeterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmartMeterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}