using e_community_cloud_lib.BusinessLogic.Implementations;
using e_community_cloud_lib.Database.General;
using e_community_cloud_lib.Util.BusinessLogic;
using System;
using System.Collections.Generic;
using System.Text;

namespace e_community_cloud_lib.Util
{
    public static class Validate
    {
        public static void MemberEmailIsConfirmed(Member user)
        {
            if (!user.EmailConfirmed)
            {
                throw new ServiceException(ServiceException.Type.EMAIL_NOT_CONFIRMED);
            }
        }
    }
}
