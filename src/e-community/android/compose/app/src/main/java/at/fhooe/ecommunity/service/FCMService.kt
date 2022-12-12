package at.fhooe.ecommunity.service

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.ECommunityApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FCMService : FirebaseMessagingService() {

    private lateinit var mApplication: ECommunityApplication

    override fun onCreate() {
        super.onCreate()
        mApplication = application as ECommunityApplication
    }

    @SuppressLint("DiscouragedApi")
    override fun onMessageReceived(_remoteMessage: RemoteMessage) {
        super.onMessageReceived(_remoteMessage)

        val it = Intent(Constants.BROADCAST_RECEIVER_NOTIFICATION)

        val title = getStringWithArgArray(
            resources.getIdentifier(_remoteMessage.notification?.titleLocalizationKey, "string", packageName),
            _remoteMessage.notification?.titleLocalizationArgs
        )
        val body = getStringWithArgArray(
            resources.getIdentifier(_remoteMessage.notification?.bodyLocalizationKey, "string", packageName),
            _remoteMessage.notification?.bodyLocalizationArgs
        )

        it.putExtra(Constants.BROADCAST_RECEIVER_EXTRA_MESSAGE, "$title: $body")
        it.putExtra(Constants.BROADCAST_RECEIVER_EXTRA_BADGE, _remoteMessage.data["badge"])

        LocalBroadcastManager.getInstance(this).sendBroadcast(it)
    }

    override fun onNewToken(token: String) {
        mApplication.cloudRESTRepository.updateFCMToken(token)
    }

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