package at.fhooe.smartmeter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.fhooe.smartmeter.data.PairingRepository
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.WifiNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WifiNetworkViewModel(pairingRepository: PairingRepository) : ViewModel() {

    private val mPairingRepository = pairingRepository
    private var _uiState = MutableStateFlow(WifiNetworkState.Empty)

    val mUiState: StateFlow<WifiNetworkState> = _uiState

    init { }

    /**
     * collect Wifi Networks from backend
     * @see Local
     */
    fun getWifiNetworks(localDevice: Local?) {
        viewModelScope.launch(Dispatchers.IO) {
            localDevice?.let { local ->
                mPairingRepository.getAvailableNetworks(local).collect {
                    _uiState.value = WifiNetworkState(isLoading = false, wifiNetworks = it)
                }
            }
        }
    }
}

/**
 * ViewModel has a state
 */
data class WifiNetworkState(
    val isLoading: Boolean,
    val wifiNetworks: List<WifiNetwork>
) {
    companion object {
        val Empty = WifiNetworkState(
            isLoading = true,
            wifiNetworks = emptyList()
        )
    }
}

/**
 * create WifiNetworkViewModel with Factory Pattern
 * @param repository backend calls
 * @see PairingRepository
 */
public class WifiNetworkViewModelFactory(private val repository: PairingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WifiNetworkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WifiNetworkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}