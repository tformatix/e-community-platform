using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace e_community_cloud_lib.Util
{
    public static class Formatter
    {
        /// <summary>
        /// formats meter data value into string representation
        /// </summary>
        /// <param name="value">meter data value</param>
        /// <param name="isEnergy">is energy and not power (power: kW, energy kWh)</param>
        /// <returns>string representation (e.g. 1.2 kWh)</returns>
        public static string formatMeterData(int value, bool isEnergy = false) {
            var firstNumber = 0;
            var commanNumber = 0.0;
            var unit = "";
            var energyUnit = isEnergy? Constants.HOUR_UNIT : "";

            if(value < Constants.KILOWATT) {
                return $"{value} {Constants.WATT_UNIT}{energyUnit}";
            } else if(value < Constants.MEGAWATT) {
                firstNumber = value / Constants.KILOWATT;
                commanNumber = ((double)value) % Constants.KILOWATT;
                unit = Constants.KILOWATT_UNIT;
            }else if (value < Constants.GIGAWATT) {
                firstNumber = value / Constants.MEGAWATT;
                commanNumber = ((double)value) % Constants.MEGAWATT;
                unit = Constants.MEGAWATT_UNIT;
            }else { 
                firstNumber = value / Constants.GIGAWATT;
                commanNumber = ((double)value) % Constants.GIGAWATT;
                unit = Constants.GIGAWATT_UNIT;
            }

            return $"{firstNumber}.{(commanNumber * 10).ToString().Substring(0,2)} {unit}{energyUnit}";
        }
    }
}
