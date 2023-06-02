using System;

namespace e_community_local_lib.Util.BusinessLogic {
    public class ServiceException : Exception {
        public ServiceException(Type _type, Exception _innerException = null) : base(_type.ToString(), _innerException) { }

        public enum Type {
            INVALID_REFRESH_TOKEN,
            SMART_METER_NULL
        }
    }
}
