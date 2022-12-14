/**
 * e-community-cloud
 *
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v1
 * 
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package at.fhooe.ecommunity.data.remote.openapi.cloud.models

import at.fhooe.ecommunity.data.remote.openapi.cloud.models.SupplyMode

import com.squareup.moshi.Json

/**
 * 
 *
 * @param id 
 * @param userName 
 * @param email 
 * @param streetNr 
 * @param zipCode 
 * @param cityName 
 * @param countryCode 
 * @param isEmailPublic 
 * @param transformerId 
 * @param substationId 
 * @param gridLevel 
 * @param supplyMode 
 */

data class MemberDto (

    @Json(name = "id")
    val id: java.util.UUID? = null,

    @Json(name = "userName")
    val userName: kotlin.String? = null,

    @Json(name = "email")
    val email: kotlin.String? = null,

    @Json(name = "streetNr")
    val streetNr: kotlin.String? = null,

    @Json(name = "zipCode")
    val zipCode: kotlin.String? = null,

    @Json(name = "cityName")
    val cityName: kotlin.String? = null,

    @Json(name = "countryCode")
    val countryCode: kotlin.String? = null,

    @Json(name = "isEmailPublic")
    val isEmailPublic: kotlin.Boolean? = null,

    @Json(name = "transformerId")
    val transformerId: kotlin.Int? = null,

    @Json(name = "substationId")
    val substationId: kotlin.Int? = null,

    @Json(name = "gridLevel")
    val gridLevel: kotlin.Int? = null,

    @Json(name = "supplyMode")
    val supplyMode: SupplyMode? = null

)

