package at.fhooe.ecommunity.ui.screen.sharing

import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel

/**
 * viewModel for Sharing Screen
 * @param _application application object
 * interaction with
 * @see CloudSignalRRepository
 */
class SharingViewModel(_application: ECommunityApplication) : LoadingStateViewModel(_application) {

    // signal r repo
    var mCloudSignalRRepository = CloudSignalRRepository()
}