package at.fhooe.smartmeter.dto


data class MeterDataRTDto(val missingSmartMeterCount: Int,
                          val missingSmartMeterCountMember: Int,
                          val timestamp: String,
                          val activePowerPlus: Int,
                          val activePowerMinus: Int,
                          val reactivePowerPlus: Int,
                          val reactivePowerMinus: Int,
                          val eCommunityActivePowerPlus: Int,
                          val eCommunityActivePowerMinus: Int,
                          val eCommunityReactivePowerPlus: Int,
                          val eCommunityReactivePowerMinus: Int

)