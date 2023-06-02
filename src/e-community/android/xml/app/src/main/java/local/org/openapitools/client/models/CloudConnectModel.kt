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

import local.org.openapitools.client.models.SmartMeterModel
import local.org.openapitools.client.models.LocalSmartMeterModel

import com.squareup.moshi.Json

/**
 * 
 *
 * @param refreshToken 
 * @param localSmartMeter 
 */

data class CloudConnectModel (

    @Json(name = "refreshToken")
    val refreshToken: kotlin.String? = null,

    @Json(name = "smartMeter")
    val localSmartMeter: SmartMeterModel? = null

)
