package at.fhooe.ecommunity.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.model.LegacyLoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * base class of view models that use LoadingState as their current state
 * @param mApplication eCommunity application
 * @see ViewModel
 */
abstract class LegacyLoadingStateViewModel(
    val mApplication: ECommunityApplication,
) : ViewModel() {
    /**
     * current state of the view model
     */
    var mState = MutableStateFlow(LegacyLoadingState(LegacyLoadingState.State.IDLE))

    /**
     * goes back to the IDLE state (after SUCCESS and ERROR)
     */
    fun backToIdle() = viewModelScope.launch {
        mState.emit(LegacyLoadingState(LegacyLoadingState.State.IDLE))
    }
}