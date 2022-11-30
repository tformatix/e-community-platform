package at.fhooe.ecommunity.model

import java.lang.Exception

/**
 * loading state of a view model
 * @param mState current state of a view model
 * @param mId (optional) a view model must process more than one action and these must be distinguished (e.g. Login and Register)
 * @param mException (optional) some exception
 */
data class LoadingState(val mState: State, val mId: Int = 0, val mException: Exception? = null) {
    /**
     * state of a view model
     */
      enum class State {
        IDLE,
        RUNNING,
        SUCCESS,
        FAILED,
    }
}