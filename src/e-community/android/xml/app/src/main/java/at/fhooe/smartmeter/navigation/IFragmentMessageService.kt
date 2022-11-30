package at.fhooe.smartmeter.navigation

import android.os.Bundle

interface IFragmentMessageService {

    /**
     * used for communication between fragment & activity
     */
    fun onCommunicate(code: Int, bundle: Bundle?)
}