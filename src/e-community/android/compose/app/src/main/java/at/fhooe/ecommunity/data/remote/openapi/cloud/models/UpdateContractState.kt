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
 * @param contractId 
 * @param updateState 
 */

data class UpdateContractState (

    @Json(name = "contractId")
    val contractId: kotlin.String? = null,

    @Json(name = "updateState")
    val updateState: kotlin.Int? = null

)

