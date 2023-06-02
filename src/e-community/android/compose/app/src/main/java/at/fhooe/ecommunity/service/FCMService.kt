package at.fhooe.ecommunity.service

import android.annotation.SuppressLint
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Firebase Cloud Messaging recipient when the app is actively open
 */
class FCMService : FirebaseMessagingService() {

    private lateinit var mApplication: ECommunityApplication

    override fun onCreate() {
        super.onCreate()
        mApplication = application as ECommunityApplication
    }

    @SuppressLint("DiscouragedApi")
    override fun onMessageReceived(_remoteMessage: RemoteMessage) {
        super.onMessageReceived(_remoteMessage)

        val it = Intent(Constants.BROADCAST_RECEIVER_NOTIFICATION) // intent to broadcast receiver (MainActivity)

        // extract title from string resources
        val title = getStringWithArgArray(
            resources.getIdentifier(_remoteMessage.notification?.titleLocalizationKey, "string", packageName),
            _remoteMessage.notification?.titleLocalizationArgs
        )
        // extract body from string resources
        val body = getStringWithArgArray(
            resources.getIdentifier(_remoteMessage.notification?.bodyLocalizationKey, "string", packageName),
            _remoteMessage.notification?.bodyLocalizationArgs
        )

        it.putExtra(Constants.BROADCAST_RECEIVER_NOTIFICATION_EXTRA_MESSAGE, "$title: $body") // message
        it.putExtra(Constants.BROADCAST_RECEIVER_NOTIFICATION_EXTRA_ID, _remoteMessage.data["id"]) // id of notification (e.g. e-community)

        LocalBroadcastManager.getInstance(this).sendBroadcast(it) // send to broadcast receiver
    }

    override fun onNewToken(_token: String) {
        // new firebase token --> send to backend
        mApplication.cloudRESTRepository.updateFCMToken(_token)
    }

    /**
     * retrieve a string from the resources
     * @param _id resource reference
     * @param _args 0 to 5 arguments put into the string
     */
    private fun getStringWithArgArray(_id: Int, _args: Array<String>?): String{
        return when(_args?.size){
            1 -> getString(_id, _args[0])
            2 -> getString(_id, _args[0], _args[1])
            3 -> getString(_id, _args[0], _args[1], _args[2])
            4 -> getString(_id, _args[0], _args[1], _args[2], _args[3])
            5 -> getString(_id, _args[0], _args[1], _args[2], _args[3], _args[4])
            else -> getString(_id)
        }
    }
}