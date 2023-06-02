package at.fhooe.ecommunity.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "blockchain_balance")
data class BlockchainBalance(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val received: String? = null,

    val sent: String? = null,

    var balance: String? = null
)