package at.fhooe.ecommunity.ui.screen.profile.pairing

import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalSmartMeterDto
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.model.Local
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * viewModel for Pairing Workflow
 * @param _application application object
 */
class PairingViewModel(_application: ECommunityApplication) : LoadingStateViewModel(_application) {

    /**
     * start the NSD Manager and discover for local devices
     * @param _local state of a discovered device
     */
    fun discoverDevices(_local: MutableState<Local>) {
        val localRESTRepository = mApplication.localRESTRepository

        // start discovery
        localRESTRepository.discover(_local)
    }
}