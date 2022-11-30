package at.fhooe.ecommunity.ui.screen.news

import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.MemberApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SmartMeterApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MemberDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalSmartMeterDto
import at.fhooe.ecommunity.data.remote.repository.CloudSignalRRepository
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LoadingStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * viewModel for News Screen
 * @param _application application object
 */
class NewsViewModel(_application: ECommunityApplication) : LoadingStateViewModel(_application) {

}