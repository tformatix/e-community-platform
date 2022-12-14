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
 * @param smartMeterId 
 * @param smartMeterName 
 * @param isNonComplianceMuted 
 * @param forecast 
 * @param projectedActiveEnergyPlus 
 */

data class MonitoringStatusDto (

    @Json(name = "smartMeterId")
    val smartMeterId: java.util.UUID? = null,

    @Json(name = "smartMeterName")
    val smartMeterName: kotlin.String? = null,

    @Json(name = "isNonComplianceMuted")
    val isNonComplianceMuted: kotlin.Boolean? = null,

    @Json(name = "forecast")
    val forecast: kotlin.Int? = null,

    @Json(name = "projectedActiveEnergyPlus")
    val projectedActiveEnergyPlus: kotlin.Int? = null

)

