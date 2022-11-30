package at.fhooe.ecommunity.data.remote.signalr.dto

/**
 * real time meter data
 * @param timestamp date and time of measuring point
 * @param missingSmartMeterCount how many smart meters are missing overall
 * @param missingSmartMeterCountMember how many smart meters are missing from a member
 * @param activePowerPlus current power P+
 * @param activePowerMinus current power P-
 * @param reactivePowerPlus current power R+
 * @param reactivePowerMinus current power R-
 * @param eCommunityActivePowerPlus current power P+ of whole eCommunity
 * @param eCommunityActivePowerMinus current power P- of whole eCommunity
 * @param eCommunityReactivePowerPlus current power R+ of whole eCommunity
 * @param eCommunityReactivePowerMinus current power R- of whole eCommunity
 */
data class MeterDataRTDto(val timestamp: String,
                          val missingSmartMeterCount: Int,
                          val missingSmartMeterCountMember: Int,
                          val activePowerPlus: Int,
                          val activePowerMinus: Int,
                          val reactivePowerPlus: Int,
                          val reactivePowerMinus: Int,
                          val eCommunityActivePowerPlus: Int,
                          val eCommunityActivePowerMinus: Int,
                          val eCommunityReactivePowerPlus: Int,
                          val eCommunityReactivePowerMinus: Int

)