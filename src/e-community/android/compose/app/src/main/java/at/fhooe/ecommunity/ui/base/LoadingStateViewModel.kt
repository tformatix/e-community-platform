package at.fhooe.ecommunity.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.fhooe.ecommunity.ECommunityApplication
import at.fhooe.ecommunity.model.LegacyLoadingState
import at.fhooe.ecommunity.model.LoadingState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * base class of view models that use LoadingState as their current state
 * @param mApplication eCommunity application
 * @see ViewModel
 */
abstract class LoadingStateViewModel(
    val mApplication: ECommunityApplication,
) : ViewModel() {
    /**
     * current state of the view model
     */
    var mState = MutableStateFlow(LoadingState(LoadingState.State.IDLE))

    /**
     * default exception handler for remote operations
     * @param _id (optional) a view model must process more than one action and these must be distinguished (e.g. Login and Register)
     * @return a CoroutineExceptionHandler
     */
    protected fun getDefaultExceptionHandler(_id: Int = -1): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, _exc ->
            emitRemoteErrorState(_exc, _id)
        }
    }

    /**
     * emits new loading state
     * @param _loadingState new loading state
     */
    protected fun emitState(_loadingState: LoadingState) {
        CoroutineScope(Dispatchers.Main).launch {
            mState.emit(_loadingState)
        }
        if (mState.value.mState == LoadingState.State.SUCCESS || mState.value.mState == LoadingState.State.FAILED) {
            viewModelScope.launch { mState.emit(LoadingState(LoadingState.State.IDLE)) }
        }
    }

    /**
     * emits new error loading state
     * @param _exception some exception
     * @param _id a view model must process more than one action and these must be distinguished (e.g. Login and Register)
     */
    protected fun emitRemoteErrorState(_exception: Throwable, _id: Int = -1) {
        emitErrorState(mApplication.remoteExceptionRepository.exceptionToString(_exception), _id)
    }

    /**
     * emits new error loading state
     * @param _exception exception message
     * @param _id a view model must process more than one action and these must be distinguished (e.g. Login and Register)
     */
    protected fun emitErrorState(_exception: String, _id: Int = -1) {
        emitState(LoadingState(LoadingState.State.FAILED, _id, _exception))
    }
}