using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using e_community_local_lib.Database.Meter;

namespace e_community_local_lib.Util.Extensions
{
    public static class DtoExtensions
    {
        /// <summary>
        /// dynamically parses a object - called like source.CopyPropertiesTo(new ObjectA())
        /// </summary>
        public static T CopyPropertiesTo<T>(
          this object _source,
          T _target,
          string[] _ignoreProperties = null)
        {
            if (_ignoreProperties == null) _ignoreProperties = new string[] { };
            var propsSource = _source.GetType().GetProperties()
              .Where(x => x.CanRead && !_ignoreProperties.Contains(x.Name));
            var propsTarget = _target.GetType().GetProperties()
              .Where(x => x.CanWrite);

            propsTarget
              .Where(prop => propsSource.Any(x => x.Name == prop.Name))
              .ToList()
              .ForEach(prop =>
              {
                  var propSource = propsSource.Where(x => x.Name == prop.Name).First();
                  prop.SetValue(_target, propSource.GetValue(_source));
              });
            return _target;
        }
        
        /// <summary>
        /// parsing from Database-Model to DTO
        /// called like source.ParseMeterData()
        /// </summary>
        /// <param name="source">Database-Model</param>
        /// <returns>DTO</returns>
        public static MeterDataBase ParseMeterData(this MeterDataBase source)
        {
            return new MeterDataBase
            {
                Id = source.Id,
                Timestamp = source.Timestamp,
                ActiveEnergyPlus = source.ActiveEnergyPlus,
                ActiveEnergyMinus = source.ActiveEnergyMinus,
                ReactiveEnergyPlus = source.ReactiveEnergyPlus,
                ReactiveEnergyMinus = source.ReactiveEnergyMinus,
                ActivePowerPlus = source.ActivePowerPlus,
                ActivePowerMinus = source.ActivePowerMinus,
                ReactivePowerPlus = source.ReactivePowerPlus,
                ReactivePowerMinus = source.ReactivePowerMinus
            };
        }
    }
}
