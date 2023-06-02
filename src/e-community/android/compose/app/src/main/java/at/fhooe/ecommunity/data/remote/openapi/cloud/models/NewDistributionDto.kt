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

import at.fhooe.ecommunity.data.remote.openapi.cloud.models.NewPortionDto

import com.squareup.moshi.Json

/**
 * 
 *
 * @param newPortions 
 * @param unassignedActiveEnergyMinus 
 * @param missingSmartMeterCount 
 */

data class NewDistributionDto (

    @Json(name = "newPortions")
    val newPortions: kotlin.collections.List<NewPortionDto>? = null,

    @Json(name = "unassignedActiveEnergyMinus")
    val unassignedActiveEnergyMinus: kotlin.Int? = null,

    @Json(name = "missingSmartMeterCount")
    val missingSmartMeterCount: kotlin.Int? = null

)
