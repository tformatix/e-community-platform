package at.fhooe.ecommunity.data.remote.signalr.dto

/**
 * real time meter data
 * @param timestamp date and time of measuring point
 * @param missingSmartMeterCount how many smart meters are missing overall
 * @param missingSmartMeterCountMember how many smart meters are missing from a member
 * @param eCommunityActivePowerPlus current power P+ of whole eCommunity
 * @param eCommunityActivePowerMinus current power P- of whole eCommunity
 * @param eCommunityReactivePowerPlus current power R+ of whole eCommunity
 * @param eCommunityReactivePowerMinus current power R- of whole eCommunity
 * @param meterDataMember meter data of member (per smart meter)
 */
data class BufferedMeterDataRTDto(val timestamp: String,
                                  val missingSmartMeterCount: Int,
                                  val missingSmartMeterCountMember: Int,
                                  val eCommunityActivePowerPlus: Int,
                                  val eCommunityActivePowerMinus: Int,
                                  val eCommunityReactivePowerPlus: Int,
                                  val eCommunityReactivePowerMinus: Int,
                                  val meterDataMember: List<MeterDataRTDto<Int>>?
)