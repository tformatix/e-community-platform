using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text;

namespace e_community_local_lib.Database.Meter
{
    /// <summary>
    /// temp table for storing the real time data (will be delete after 90s)
    /// <see cref="MeterDataBase"/>
    /// </summary>
    public class MeterDataRealTime : MeterDataBase {}
}
