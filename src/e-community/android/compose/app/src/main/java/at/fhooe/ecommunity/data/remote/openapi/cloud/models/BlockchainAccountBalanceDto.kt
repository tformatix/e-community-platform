package at.fhooe.ecommunity.data.remote.openapi.cloud.models

import com.squareup.moshi.Json

/**
 * @param received
 * @param sent
 * @param balance
 */

data class BlockchainAccountBalanceDto(
    @Json(name = "received")
    val received: kotlin.String? = null,

    @Json(name = "sent")
    val sent: kotlin.String? = null,

    @Json(name = "balance")
    var balance: kotlin.String? = null
)