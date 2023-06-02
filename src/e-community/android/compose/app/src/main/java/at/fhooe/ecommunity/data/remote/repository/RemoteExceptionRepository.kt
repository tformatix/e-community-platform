package at.fhooe.ecommunity.data.remote.repository

import android.content.Context
import android.util.Log
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ClientError
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ClientException
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ServerException
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ErrorDto
import at.fhooe.ecommunity.model.RemoteException
import com.google.gson.Gson
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * contains methods for handling remote exceptions
 * @param mContext some context
 */
class RemoteExceptionRepository(private val mContext: Context) {

    /**
     * @param _exception some exception from the remote backend
     * @return a RemoteException object to the given exception
     */
    fun exceptionToRemoteException(_exception: Throwable): RemoteException {
        when (_exception) {
            is ClientException -> {
                if (_exception.statusCode == 403)
                    return RemoteException(RemoteException.Type.UNAUTHORIZED, RemoteException.Category.CLIENT_ERROR)
                else if(_exception.statusCode == 404)
                    return RemoteException(RemoteException.Type.NOT_FOUND, RemoteException.Category.CLIENT_ERROR)

                if (_exception.response is ClientError<*>) {
                    if (_exception.response.body is String) {
                        Gson().fromJson(_exception.response.body, ErrorDto::class.java).error?.let { error ->
                            try {
                                return RemoteException(RemoteException.Type.valueOf(error), RemoteException.Category.CLIENT_ERROR)
                            } catch (_: Exception) {
                                return RemoteException(RemoteException.Type.UNEXPECTED)
                            }
                        }
                    }
                }
            }
            is ServerException -> return RemoteException(RemoteException.Type.SERVER_ERROR, RemoteException.Category.SERVER_ERROR)
            is UnknownHostException -> return RemoteException(RemoteException.Type.NO_INTERNET)
            is SocketTimeoutException -> return RemoteException(RemoteException.Type.SERVER_UNREACHABLE)
        }
        return RemoteException(RemoteException.Type.UNEXPECTED)
    }

    /**
     * @param _exception some exception from the remote backend
     * @return a String to the given exception
     */
    fun exceptionToString(_exception: Throwable): String {
        return remoteExceptionToString(exceptionToRemoteException(_exception))
    }

    /**
     * @param _remoteException a RemoteException object to a exception
     * @return a String to the given RemoteException
     */
    fun remoteExceptionToString(_remoteException: RemoteException): String {
        val errorStr = when (_remoteException.mType) {
            RemoteException.Type.UNAUTHORIZED -> mContext.getString(R.string.error_403)
            RemoteException.Type.NOT_FOUND -> mContext.getString(R.string.error_404)
            RemoteException.Type.SERVER_ERROR -> mContext.getString(R.string.error_500)
            RemoteException.Type.NO_INTERNET -> mContext.getString(R.string.error_no_internet)
            RemoteException.Type.SERVER_UNREACHABLE -> mContext.getString(R.string.error_server_unreachable)
            RemoteException.Type.UNEXPECTED -> mContext.getString(R.string.error_unexpected)
            RemoteException.Type.CREATE_ACCOUNT_FAILED -> mContext.getString(R.string.error_400_create_account_failed)
            RemoteException.Type.INVALID_CREDENTIALS -> mContext.getString(R.string.error_400_invalid_credentials)
            RemoteException.Type.INVALID_REFRESH_TOKEN -> mContext.getString(R.string.error_400_invalid_refresh_token)
            RemoteException.Type.COULDNT_SEND_EMAIL -> mContext.getString(R.string.error_400_couldnt_send_email)
            RemoteException.Type.EMAIL_NOT_CONFIRMED -> mContext.getString(R.string.error_400_email_not_confirmed)
            RemoteException.Type.EMAIL_CONFIRM_FAILED -> mContext.getString(R.string.error_400_email_confirm_failed)
            RemoteException.Type.EMAIL_ALREADY_CONFIRMED -> mContext.getString(R.string.error_400_email_already_confirmed)
            RemoteException.Type.ACCOUNT_LOCKED -> mContext.getString(R.string.error_400_account_locked)
            RemoteException.Type.EMAIL_TAKEN -> mContext.getString(R.string.error_400_email_taken)
            RemoteException.Type.RESET_PASSWORD_FAILED -> mContext.getString(R.string.error_400_reset_password_failed)
            RemoteException.Type.CHANGE_PASSWORD_FAILED -> mContext.getString(R.string.error_400_change_password_failed)
            RemoteException.Type.NO_SMART_METER_FOUND -> mContext.getString(R.string.error_400_no_smart_meter_found)
            RemoteException.Type.MEMBER_ID_NOT_RESOLVABLE -> mContext.getString(R.string.error_400_member_id_not_resolvable)
            RemoteException.Type.SMART_METER_NOT_TO_USER -> mContext.getString(R.string.error_400_smart_meter_not_to_user)
            RemoteException.Type.PORTION_ACK_EXPIRED -> mContext.getString(R.string.error_400_portion_ack_expired)
        }
        Log.e(TAG, "ERROR::${_remoteException}:$errorStr")
        return errorStr
    }
}