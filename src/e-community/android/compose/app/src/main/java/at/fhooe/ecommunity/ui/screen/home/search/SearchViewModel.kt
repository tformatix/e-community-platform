package at.fhooe.ecommunity.ui.screen.home.search

import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.apis.SearchApi
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ECommunityDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MemberDto
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.base.LegacyLoadingStateViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * viewModel for News Screen
 * @param _application application object
 */
class SearchViewModel(_application: ECommunityApplication) : LegacyLoadingStateViewModel(_application) {

    val mUserSearchResults = MutableStateFlow(List(init = { MemberDto() }, size = 0))
    val mECommSearchResults = MutableStateFlow(List(init = { ECommunityDto() }, size = 0))

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
                    if (_searchQuery.value.userSearch) {
                        mUserSearchResults.value = searchApi.searchSearchForUserGet(_searchQuery.value.query)
                    }
                    if (_searchQuery.value.eCommSearch) {
                        mECommSearchResults.value = searchApi.searchSearchForECommsGet(_searchQuery.value.query)
                    }

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