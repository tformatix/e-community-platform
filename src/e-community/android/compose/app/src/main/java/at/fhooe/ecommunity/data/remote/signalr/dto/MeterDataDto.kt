package at.fhooe.ecommunity.data.remote.signalr.dto

/**
 * meter data
 * @param timestamp date and time of measuring point
 * @param activeEnergyPlus meter reading energy A+
 * @param activeEnergyMinus meter reading energy A-
 * @param reactiveEnergyPlus meter reading energy R+
 * @param reactiveEnergyMinus meter reading energy R-
 * @param activePowerPlus current power P+
 * @param activePowerMinus current power P-
 * @param reactivePowerPlus current power R+
 * @param reactivePowerMinus current power R-
 */
data class MeterDataDto<T> (val id: Int,
                            val timestamp: String,
                            var activeEnergyPlus: T,
                            var activeEnergyMinus: T,
                            var reactiveEnergyPlus: T,
                            var reactiveEnergyMinus: T,
                            var activePowerPlus: T,
                            var activePowerMinus: T,
                            var reactivePowerPlus: T,
                            var reactivePowerMinus: T,)