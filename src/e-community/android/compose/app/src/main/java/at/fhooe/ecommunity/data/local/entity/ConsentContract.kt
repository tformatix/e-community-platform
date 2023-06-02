package at.fhooe.ecommunity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

/**
 *
 *
 * @param contractId
 * @param state
 * @param addressContract
 * @param addressProposer
 * @param addressConsenter
 * @param startEnergyData
 * @param endEnergyData
 * @param validityOfContract
 * @param pricePerHour
 * @param totalPrice
 */

@Entity(tableName = "contract")
data class ConsentContract (

    @PrimaryKey
    @SerializedName("ContractId")
    var contractId: kotlin.String,

    @SerializedName("State")
    var state: kotlin.Int? = null,

    @SerializedName("AddressContract")
    val addressContract: kotlin.String? = null,

    @SerializedName("AddressProposer")
    val addressProposer: kotlin.String? = null,

    @SerializedName("AddressConsenter")
    val addressConsenter: kotlin.String? = null,

    @SerializedName("StartEnergyData")
    val startEnergyData: kotlin.String? = null,

    @SerializedName("EndEnergyData")
    val endEnergyData: kotlin.String? = null,

    @SerializedName("ValidityOfContract")
    val validityOfContract: kotlin.String? = null,

    @SerializedName("PricePerHour")
    val pricePerHour: kotlin.String? = null,

    @SerializedName("TotalPrice")
    val totalPrice: kotlin.String? = null,

    @SerializedName("DataUsage")
    val dataUsage: kotlin.Int? = null,

    @SerializedName("TimeResolution")
    val timeResolution: kotlin.Int? = null
)