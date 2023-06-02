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
 * @param activeEnergyPlus 
 * @param activeEnergyMinus 
 */

data class MeterDataMonitoringModel (

    @Json(name = "smartMeterId")
    val smartMeterId: java.util.UUID? = null,

    @Json(name = "activeEnergyPlus")
    val activeEnergyPlus: kotlin.Int? = null,

    @Json(name = "activeEnergyMinus")
    val activeEnergyMinus: kotlin.Int? = null

)
