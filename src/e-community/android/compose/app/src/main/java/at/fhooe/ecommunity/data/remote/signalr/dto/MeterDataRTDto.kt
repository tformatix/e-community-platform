package at.fhooe.ecommunity.data.remote.signalr.dto

import java.util.*

/**
 * meter data
 * @param smartMeterId id of smart meter
 * @param memberId id of member
 * @param activePowerPlus current power P+
 * @param activePowerMinus current power P-
 * @param reactivePowerPlus current power R+
 * @param reactivePowerMinus current power R-
 */
data class MeterDataRTDto<T> (var smartMeterId: UUID,
                              var memberId: UUID,
                              var activePowerPlus: T,
                              var activePowerMinus: T,
                              var reactivePowerPlus: T,
                              var reactivePowerMinus: T,)