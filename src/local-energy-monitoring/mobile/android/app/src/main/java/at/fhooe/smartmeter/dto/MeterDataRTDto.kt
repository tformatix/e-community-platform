package at.fhooe.smartmeter.dto

data class MeterDataRTDto (var unit: MeterDataDto<String>, var meterDataValues: MeterDataDto<Double> ){
    override fun toString(): String {
        return "${meterDataValues.activePowerPlus.toInt()} ${unit.activePowerPlus}"
    }
}