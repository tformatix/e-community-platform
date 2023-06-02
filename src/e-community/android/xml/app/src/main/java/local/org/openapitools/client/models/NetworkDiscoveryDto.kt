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

package local.org.openapitools.client.models


import com.squareup.moshi.Json

/**
 * 
 *
 * @param ssid 
 * @param quality 
 */

data class NetworkDiscoveryDto (

    @Json(name = "ssid")
    val ssid: kotlin.String? = null,

    @Json(name = "quality")
    val quality: kotlin.String? = null

)
