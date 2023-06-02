package at.fhooe.smartmeter.dto

data class MeterDataHistDto (var Unit: MeterDataDto<String>, var Min: MeterDataDto<Double>, var Avg: MeterDataDto<Double>, var Max: MeterDataDto<Double>, var MeterDataValues: ArrayList<MeterDataDto<Double>>)