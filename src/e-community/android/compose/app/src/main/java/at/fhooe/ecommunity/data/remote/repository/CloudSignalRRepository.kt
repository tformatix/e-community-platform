package at.fhooe.ecommunity.data.remote.repository

import at.fhooe.ecommunity.Constants
import com.google.gson.internal.LinkedTreeMap
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Cloud SignalR functionality
 */
class CloudSignalRRepository {

    /**
     * connection to SignalR hub
     */
    private var mHubConnection: HubConnection? = null

    /**
     * build connection to the hub
     * @param _url url of SignalR hub
     * @param _token access token
     * @param _method entry method for SignalR hub
     * @param _action handle SignalR data; JSONObject(data.toMap()) for JSON data
     */
    fun initialize(_url: String, _token: String, _method: String, _action: Action1<LinkedTreeMap<*, *>>) {
        mHubConnection = HubConnectionBuilder
            .create(_url)
            .withHeader(Constants.HTTP_AUTHORIZATION_KEY, "${Constants.HTTP_AUTHORIZATION_PREFIX} $_token")
            .build()

        mHubConnection?.on(_method, _action, LinkedTreeMap::class.java)
    }

    /**
     * start connection to Hub
     * @return successful or failure
     */
    fun startConnection(): Boolean {
        if (mHubConnection?.connectionState == HubConnectionState.DISCONNECTED) {
            mHubConnection?.start()
            return true
        }
        return false
    }

    /**
     * stop connection to Hub
     * @return successful or failure
     */
    fun stopConnection(): Boolean {
        if (mHubConnection?.connectionState == HubConnectionState.CONNECTED) {
            mHubConnection?.stop()
            return true
        }
        return false
    }

    /**
     * @return the HubConnectionState
     */
    fun getConnectionState(): HubConnectionState {
        return if (mHubConnection == null) {
            HubConnectionState.DISCONNECTED
        } else {
            mHubConnection?.connectionState!!
        }
    }
}