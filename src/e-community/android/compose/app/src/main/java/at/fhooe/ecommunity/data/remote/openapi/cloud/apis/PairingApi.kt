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

package at.fhooe.ecommunity.data.remote.openapi.cloud.apis

import java.io.IOException

import at.fhooe.ecommunity.data.remote.openapi.cloud.models.CreateSmartMeterDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.CreateSmartMeterModel
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ErrorDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.LocalDto

import com.squareup.moshi.Json

import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ApiClient
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ApiResponse
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ClientException
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ClientError
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ServerException
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ServerError
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.MultiValueMap
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.RequestConfig
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.RequestMethod
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.ResponseType
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.Success
import at.fhooe.ecommunity.data.remote.openapi.cloud.infrastructure.toMultiValue

class PairingApi(basePath: kotlin.String = defaultBasePath) : ApiClient(basePath) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty(ApiClient.baseUrlKey, "http://localhost")
        }
    }

    /**
    * 
    * 
    * @param createSmartMeterModel  (optional)
    * @return CreateSmartMeterDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pairingCreateSmartMeterPost(createSmartMeterModel: CreateSmartMeterModel?) : CreateSmartMeterDto {
        val localVarResponse = pairingCreateSmartMeterPostWithHttpInfo(createSmartMeterModel = createSmartMeterModel)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as CreateSmartMeterDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * 
    * 
    * @param createSmartMeterModel  (optional)
    * @return ApiResponse<CreateSmartMeterDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun pairingCreateSmartMeterPostWithHttpInfo(createSmartMeterModel: CreateSmartMeterModel?) : ApiResponse<CreateSmartMeterDto?> {
        val localVariableConfig = pairingCreateSmartMeterPostRequestConfig(createSmartMeterModel = createSmartMeterModel)

        return request<CreateSmartMeterModel, CreateSmartMeterDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation pairingCreateSmartMeterPost
    *
    * @param createSmartMeterModel  (optional)
    * @return RequestConfig
    */
    fun pairingCreateSmartMeterPostRequestConfig(createSmartMeterModel: CreateSmartMeterModel?) : RequestConfig<CreateSmartMeterModel> {
        val localVariableBody = createSmartMeterModel
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.POST,
            path = "/Pairing/CreateSmartMeter",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

    /**
    * 
    * 
    * @param smartMeterId  (optional)
    * @return LocalDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pairingLocalDataGet(smartMeterId: java.util.UUID?) : LocalDto {
        val localVarResponse = pairingLocalDataGetWithHttpInfo(smartMeterId = smartMeterId)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as LocalDto
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * 
    * 
    * @param smartMeterId  (optional)
    * @return ApiResponse<LocalDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun pairingLocalDataGetWithHttpInfo(smartMeterId: java.util.UUID?) : ApiResponse<LocalDto?> {
        val localVariableConfig = pairingLocalDataGetRequestConfig(smartMeterId = smartMeterId)

        return request<Unit, LocalDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation pairingLocalDataGet
    *
    * @param smartMeterId  (optional)
    * @return RequestConfig
    */
    fun pairingLocalDataGetRequestConfig(smartMeterId: java.util.UUID?) : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, kotlin.collections.List<kotlin.String>>()
            .apply {
                if (smartMeterId != null) {
                    put("_smartMeterId", listOf(smartMeterId.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/Pairing/LocalData",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

}
