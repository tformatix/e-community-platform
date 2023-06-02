package at.fhooe.smartmeter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "member")
data class Member(
    @PrimaryKey var memberId: String,
    var username: String,
    var email: String,
    var password: String,
    var accessToken: String,
    var refreshToken: String,
    var languageName: String)



