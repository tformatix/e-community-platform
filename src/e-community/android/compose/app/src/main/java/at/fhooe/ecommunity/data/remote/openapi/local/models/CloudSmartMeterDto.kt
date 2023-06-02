/**
 * e-community-local
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

package at.fhooe.ecommunity.data.remote.openapi.local.models


import com.squareup.moshi.Json

/**
 * 
 *
 * @param id 
 * @param aesKey 
 * @param apiKey 
 * @param name 
 * @param description 
 * @param isMain 
 * @param measuresConsumption 
 * @param measuresFeedIn 
 * @param isDirectFeedIn 
 * @param isOverflowFeedIn 
 * @param localStorageId 
 */

data class CloudSmartMeterDto (

    @Json(name = "id")
    val id: java.util.UUID? = null,

    @Json(name = "aesKey")
    val aesKey: kotlin.String? = null,

    @Json(name = "apiKey")
    val apiKey: kotlin.String? = null,

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
    val isOverflowFeedIn: kotlin.Boolean? = null,

    @Json(name = "localStorageId")
    val localStorageId: java.util.UUID? = null

)
