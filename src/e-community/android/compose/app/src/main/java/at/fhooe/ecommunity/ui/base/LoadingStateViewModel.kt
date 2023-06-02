package at.fhooe.ecommunity.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.fhooe.ecommunity.ECommunityApplication
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
abstract class LoadingStateViewModel(val mApplication: ECommunityApplication) : ViewModel() {
    /**
     * current state of the view model
     */
    var mState = LoadingState(LoadingState.State.IDLE)
        private set

    /**
     * listeners of view model
     */
    private var mListener: ((_loadingState: LoadingState) -> Unit)? = null

    /**
     * registers new listener
     * @param _listener new listener lambda
     */
    fun registerListener(_listener: (_loadingState: LoadingState) -> Unit) {
        mListener = _listener
    }

    /**
     * unregisters listener
     */
    fun unregisterListener() {
        mListener = null
    }

    /**
     * @return if a listener is registered
     */
    fun isListenerRegistered(): Boolean {
        return mListener != null
    }

    /**
     * default exception handler for remote operations
     * @param _id (optional) a view model must process more than one action and these must be distinguished (e.g. Login and Register)
     * @return a CoroutineExceptionHandler
     */
    protected fun getDefaultExceptionHandler(_id: Int = -1): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, _exc ->
            emitState(LoadingState(LoadingState.State.FAILED, _id, _exc))
        }
    }

    /**
     * emits new loading state
     * @param _loadingState new loading state
     */
    protected fun emitState(_loadingState: LoadingState) {
        mState = _loadingState
        CoroutineScope(Dispatchers.Main).launch {
            mListener?.let { it(_loadingState) }
        }
        if (mState.mState == LoadingState.State.SUCCESS || mState.mState == LoadingState.State.FAILED) {
            mState = LoadingState(LoadingState.State.IDLE)
        }
    }
}