package at.fhooe.ecommunity.ui.screen.home.search.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SearchApi
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import at.fhooe.ecommunity.ui.screen.home.search.SearchQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * viewModel for News Screen
 * @param _application application object
 */
class SearchProfileViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {

    /**
     * @param _searchQuery contains the query and user/eComm Filters
     */
    fun makeQuery(_searchQuery: MutableState<SearchQuery>) {

        val cloudRESTRepository = mApplication.cloudRESTRepository

        cloudRESTRepository.authorizedBackendCall(null) { token ->
            CoroutineScope(Dispatchers.IO).launch {
                mState.emit(LoadingState(LoadingState.State.RUNNING))

                val searchApi = SearchApi(Constants.HTTP_BASE_URL_CLOUD)

                try {

                    mState.emit(LoadingState(LoadingState.State.SUCCESS))
                }
                catch (_e: Exception) {
                    Log.e(TAG, _e.toString())
                    mState.emit(LoadingState(LoadingState.State.FAILED, mException = _e))
                }
            }
        }
    }
}