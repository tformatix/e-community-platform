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

import at.fhooe.ecommunity.data.remote.openapi.cloud.models.ErrorDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.OkDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.RequestHistoryModel

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

class HistoryApi(basePath: kotlin.String = defaultBasePath) : ApiClient(basePath) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty(ApiClient.baseUrlKey, "http://localhost")
        }
    }

    /**
    * 
    * 
    * @param requestHistoryModel  (optional)
    * @return OkDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun historyGetHistoryPost(requestHistoryModel: RequestHistoryModel?) : OkDto {
        val localVarResponse = historyGetHistoryPostWithHttpInfo(requestHistoryModel = requestHistoryModel)

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as OkDto
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
    * @param requestHistoryModel  (optional)
    * @return ApiResponse<OkDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun historyGetHistoryPostWithHttpInfo(requestHistoryModel: RequestHistoryModel?) : ApiResponse<OkDto?> {
        val localVariableConfig = historyGetHistoryPostRequestConfig(requestHistoryModel = requestHistoryModel)

        return request<RequestHistoryModel, OkDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation historyGetHistoryPost
    *
    * @param requestHistoryModel  (optional)
    * @return RequestConfig
    */
    fun historyGetHistoryPostRequestConfig(requestHistoryModel: RequestHistoryModel?) : RequestConfig<RequestHistoryModel> {
        val localVariableBody = requestHistoryModel
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.POST,
            path = "/History/GetHistory",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

}
