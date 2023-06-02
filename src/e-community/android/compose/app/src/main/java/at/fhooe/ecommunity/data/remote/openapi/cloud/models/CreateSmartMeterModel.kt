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


import com.squareup.moshi.Json

/**
 * 
 *
 * @param refreshToken 
 * @param name 
 * @param description 
 * @param isMain 
 * @param measuresConsumption 
 * @param measuresFeedIn 
 * @param isDirectFeedIn 
 * @param isOverflowFeedIn 
 */

data class CreateSmartMeterModel (

    @Json(name = "refreshToken")
    val refreshToken: kotlin.String? = null,

    @Json(name = "name")
    val name: kotlin.String? = null,

    @Json(name = "description")
    val description: kotlin.String? = null,

    @Json(name = "isMain")
    val isMain: kotlin.Boolean? = null,

    @Json(name = "measuresConsumption")
    val measuresConsumption: kotlin.Boolean? = null,

    @Json(name = "measuresFeedIn")
    val measuresFeedIn: kotlin.Boolean? = null,

    @Json(name = "isDirectFeedIn")
    val isDirectFeedIn: kotlin.Boolean? = null,

    @Json(name = "isOverflowFeedIn")
    val isOverflowFeedIn: kotlin.Boolean? = null

)
