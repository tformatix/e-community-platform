package at.fhooe.smartmeter.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.fhooe.smartmeter.models.Tile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TileViewModel(private val repository: TileRepository) : ViewModel() {

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(tile: Tile) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(tile)
    }

    fun insert(tiles: Iterable<Tile>) = viewModelScope.launch(Dispatchers.Default) {
        repository.insert(tiles)
    }

    fun update(tile: Tile) = viewModelScope.launch(Dispatchers.Default) {
        repository.update(tile)
    }

    fun update(tiles: Iterable<Tile>) = viewModelScope.launch(Dispatchers.Default) {
        repository.update(tiles)
    }

    /**
     * returns a LivaData list of the tiles
     */
    fun getTiles(): Flow<List<Tile>> {
        return repository.getTiles()
    }

}

class TileViewModelFactory(private val repository: TileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}