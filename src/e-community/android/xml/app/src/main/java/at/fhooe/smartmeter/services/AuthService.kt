package at.fhooe.smartmeter.services

import android.content.Context
import android.util.Log
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.models.Member
import org.json.JSONObject
import java.util.*


class AuthService(context: Context) : Observable() {
    private val mContext = context
    private var mTag = "AuthService"

    /**
     * attempts to login the user
     */
    @Deprecated("do not use volley")
    fun login(member: Member) {
    }

    /**
     * sends a password forgotten request
     */
    @Deprecated("do not use volley")
    fun passwordForgotten(email: String) {

    }
}