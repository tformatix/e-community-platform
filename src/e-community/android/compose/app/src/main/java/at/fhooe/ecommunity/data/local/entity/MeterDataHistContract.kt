package at.fhooe.ecommunity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "meter_data_hist_contract")
data class MeterDataHistContract(

    @PrimaryKey(autoGenerate = true)
    @SerializedName("EnergyId")
    val id: Int? = null,

    var contractId: String? = null,

    @SerializedName("id")
    val dataId: Int? = null,

    @SerializedName("timestamp")
    val timeStamp: String? = null,

    val activeEnergyPlus: Int? = null,

    val activeEnergyMinus: Int? = null,

    val reactiveEnergyPlus: Int? = null,

    val reactiveEnergyMinus: Int? = null,

    val activePowerPlus: Int? = null,

    val activePowerMinus: Int? = null,

    val reactivePowerPlus: Int? = null,

    val reactivePowerMinus: Int? = null,

    val prepaymentCounter: Int? = null
)