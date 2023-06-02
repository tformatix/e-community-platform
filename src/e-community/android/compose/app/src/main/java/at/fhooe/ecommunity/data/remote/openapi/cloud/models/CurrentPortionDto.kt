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
 * @param estimatedActiveEnergyPlus 
 * @param flexibility 
 * @param deviation 
 * @param unassignedActiveEnergyMinus 
 * @param missingSmartMeterCount 
 */

data class CurrentPortionDto (

    @Json(name = "estimatedActiveEnergyPlus")
    val estimatedActiveEnergyPlus: kotlin.Int? = null,

    @Json(name = "flexibility")
    val flexibility: kotlin.Int? = null,

    @Json(name = "deviation")
    val deviation: kotlin.Int? = null,

    @Json(name = "unassignedActiveEnergyMinus")
    val unassignedActiveEnergyMinus: kotlin.Int? = null,

    @Json(name = "missingSmartMeterCount")
    val missingSmartMeterCount: kotlin.Int? = null

)

