using System;

namespace e_community_cloud_lib.Util.BusinessLogic
{
    public class ServiceException : Exception
    {
        public ServiceException(Type _type, Exception _innerException = null) : base(_type.ToString(), _innerException) { }

        public enum Type {
            CREATE_ACCOUNT_FAILED,
            INVALID_CREDENTIALS,
            INVALID_REFRESH_TOKEN,
            COULDNT_SEND_EMAIL,
            EMAIL_NOT_CONFIRMED,
            EMAIL_CONFIRM_FAILED,
            EMAIL_ALREADY_CONFIRMED,
            ACCOUNT_LOCKED,
            EMAIL_TAKEN,
            RESET_PASSWORD_FAILED,
            CHANGE_PASSWORD_FAILED,
            NO_SMART_METER_FOUND,
            MEMBER_ID_NOT_RESOLVABLE,
            SMART_METER_NOT_TO_USER,
            PORTION_ACK_EXPIRED,
        }
    }
}
