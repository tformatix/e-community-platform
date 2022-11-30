package at.fhooe.smartmeter.dto

import java.sql.Timestamp

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
