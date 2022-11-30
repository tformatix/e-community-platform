package at.fhooe.smartmeter.api

import at.fhooe.smartmeter.dto.MeterDataRTDto
import com.google.gson.internal.LinkedTreeMap
import com.microsoft.signalr.Action1
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import org.json.JSONObject

/**
 * action - handle SignalR data; JSONObject(data.toMap()) for JSON data
 * url - url of SignalR hub
 * method - entry method for SignalR hub
 */
class SignalRListener(url: String, method:String, action: Action1<LinkedTreeMap<*, *>>) {
//    /**
//     * creating a Singleton
//     */
//    companion object {
//        @Volatile
//        private var INSTANCE: SignalRListener? = null
//        fun getInstance(url: String, method:String, action: Action1<LinkedTreeMap<*, *>>) =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: SignalRListener(url, method, action).also {
//                    INSTANCE = it
//                }
//            }
//    }
    private var hubConnection: HubConnection

    /**
     * build connection to the hub
     */
    init {
        hubConnection = HubConnectionBuilder.create(url).build()
        hubConnection.on(method, action, LinkedTreeMap::class.java)
    }

    /**
     * start connection to Hub
     */
    fun startConnection(): Boolean {
        if (hubConnection.connectionState == HubConnectionState.DISCONNECTED) {
            hubConnection.start()
            return true
        }
        return false
    }

    /**
     * stop connection to Hub
     */
    fun stopConnection(): Boolean {
        if (hubConnection.connectionState == HubConnectionState.CONNECTED) {
            hubConnection.stop()
            return true
        }
        return false
    }
}