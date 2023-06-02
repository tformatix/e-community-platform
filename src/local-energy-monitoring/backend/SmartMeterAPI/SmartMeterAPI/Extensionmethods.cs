using SmartMeterAPI.DTOs;
using SmartMeterAPIDb;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace SmartMeterAPI
{
    public static class Extensionmethods
    {
        /// <summary>
        /// dynamically parses a object - called like source.CopyPropertiesTo(new ObjectA())
        /// </summary>
        public static T CopyPropertiesTo<T>(
          this object source,
          T target,
          string[] ignoreProperties = null)
        {
            if (ignoreProperties == null) ignoreProperties = new string[] { };
            var propsSource = source.GetType().GetProperties()
              .Where(x => x.CanRead && !ignoreProperties.Contains(x.Name));
            var propsTarget = target.GetType().GetProperties()
              .Where(x => x.CanWrite);

            propsTarget
              .Where(prop => propsSource.Any(x => x.Name == prop.Name))
              .ToList()
              .ForEach(prop =>
              {
                  var propSource = propsSource.Where(x => x.Name == prop.Name).First();
                  prop.SetValue(target, propSource.GetValue(source));
              });
            return target;
        }

        /// <summary>
        /// parsing from Database-Model to DTO
        /// called like source.ParseMeterData()
        /// </summary>
        /// <param name="source">Database-Model</param>
        /// <returns>DTO</returns>
        public static MeterDataDto<double> ParseMeterData(this METERDATA source)
        {
            return new MeterDataDto<double>
            {
                Id = source.ID,
                Timestamp = source.TIMESTAMP,
                ActiveEnergyPlus = source.ACTIVE_ENERGY_PLUS,
                ActiveEnergyMinus = source.ACTIVE_ENERGY_MINUS,
                ReactiveEnergyPlus = source.REACTIVE_ENERGY_PLUS,
                ReactiveEnergyMinus = source.REACTIVE_ENERGY_MINUS,
                ActivePowerPlus = source.ACTIVE_POWER_PLUS,
                ActivePowerMinus = source.ACTIVE_POWER_MINUS,
                ReactivePowerPlus = source.REACTIVE_POWER_PLUS,
                ReactivePowerMinus = source.REACTIVE_POWER_MINUS
            };
        }
    }
}
