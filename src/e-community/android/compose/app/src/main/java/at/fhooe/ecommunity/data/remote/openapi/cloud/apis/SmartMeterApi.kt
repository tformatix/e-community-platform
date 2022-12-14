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
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalSmartMeterDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.OkDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.SmartMeterDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.UpdateSmartMeterModel

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

class SmartMeterApi(basePath: kotlin.String = defaultBasePath) : ApiClient(basePath) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty(ApiClient.baseUrlKey, "http://localhost")
        }
    }

    /**
    * 
    * 
    * @return OkDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun smartMeterExtendRTDataGet() : OkDto {
        val localVarResponse = smartMeterExtendRTDataGetWithHttpInfo()

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
    * @return ApiResponse<OkDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun smartMeterExtendRTDataGetWithHttpInfo() : ApiResponse<OkDto?> {
        val localVariableConfig = smartMeterExtendRTDataGetRequestConfig()

        return request<Unit, OkDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation smartMeterExtendRTDataGet
    *
    * @return RequestConfig
    */
    fun smartMeterExtendRTDataGetRequestConfig() : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/SmartMeter/ExtendRTData",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

    /**
    * 
    * 
    * @return kotlin.collections.List<MinimalSmartMeterDto>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun smartMeterGetMinimalSmartMetersGet() : kotlin.collections.List<MinimalSmartMeterDto> {
        val localVarResponse = smartMeterGetMinimalSmartMetersGetWithHttpInfo()

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.collections.List<MinimalSmartMeterDto>
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
    * @return ApiResponse<kotlin.collections.List<MinimalSmartMeterDto>?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun smartMeterGetMinimalSmartMetersGetWithHttpInfo() : ApiResponse<kotlin.collections.List<MinimalSmartMeterDto>?> {
        val localVariableConfig = smartMeterGetMinimalSmartMetersGetRequestConfig()

        return request<Unit, kotlin.collections.List<MinimalSmartMeterDto>>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation smartMeterGetMinimalSmartMetersGet
    *
    * @return RequestConfig
    */
    fun smartMeterGetMinimalSmartMetersGetRequestConfig() : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/SmartMeter/GetMinimalSmartMeters",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

    /**
    * 
    * 
    * @return kotlin.collections.List<SmartMeterDto>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun smartMeterGetSmartMetersGet() : kotlin.collections.List<SmartMeterDto> {
        val localVarResponse = smartMeterGetSmartMetersGetWithHttpInfo()

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.collections.List<SmartMeterDto>
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
    * @return ApiResponse<kotlin.collections.List<SmartMeterDto>?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun smartMeterGetSmartMetersGetWithHttpInfo() : ApiResponse<kotlin.collections.List<SmartMeterDto>?> {
        val localVariableConfig = smartMeterGetSmartMetersGetRequestConfig()

        return request<Unit, kotlin.collections.List<SmartMeterDto>>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation smartMeterGetSmartMetersGet
    *
    * @return RequestConfig
    */
    fun smartMeterGetSmartMetersGetRequestConfig() : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/SmartMeter/GetSmartMeters",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

    /**
    * 
    * 
    * @return OkDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun smartMeterRequestRTDataGet() : OkDto {
        val localVarResponse = smartMeterRequestRTDataGetWithHttpInfo()

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
    * @return ApiResponse<OkDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun smartMeterRequestRTDataGetWithHttpInfo() : ApiResponse<OkDto?> {
        val localVariableConfig = smartMeterRequestRTDataGetRequestConfig()

        return request<Unit, OkDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation smartMeterRequestRTDataGet
    *
    * @return RequestConfig
    */
    fun smartMeterRequestRTDataGetRequestConfig() : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/SmartMeter/RequestRTData",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

    /**
    * 
    * 
    * @return OkDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun smartMeterStopRTDataGet() : OkDto {
        val localVarResponse = smartMeterStopRTDataGetWithHttpInfo()

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
    * @return ApiResponse<OkDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun smartMeterStopRTDataGetWithHttpInfo() : ApiResponse<OkDto?> {
        val localVariableConfig = smartMeterStopRTDataGetRequestConfig()

        return request<Unit, OkDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation smartMeterStopRTDataGet
    *
    * @return RequestConfig
    */
    fun smartMeterStopRTDataGetRequestConfig() : RequestConfig<Unit> {
        val localVariableBody = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.GET,
            path = "/SmartMeter/StopRTData",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

    /**
    * 
    * 
    * @param updateSmartMeterModel  (optional)
    * @return OkDto
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class, UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun smartMeterUpdatePut(updateSmartMeterModel: UpdateSmartMeterModel?) : OkDto {
        val localVarResponse = smartMeterUpdatePutWithHttpInfo(updateSmartMeterModel = updateSmartMeterModel)

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
    * @param updateSmartMeterModel  (optional)
    * @return ApiResponse<OkDto?>
    * @throws IllegalStateException If the request is not correctly configured
    * @throws IOException Rethrows the OkHttp execute method exception
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(IllegalStateException::class, IOException::class)
    fun smartMeterUpdatePutWithHttpInfo(updateSmartMeterModel: UpdateSmartMeterModel?) : ApiResponse<OkDto?> {
        val localVariableConfig = smartMeterUpdatePutRequestConfig(updateSmartMeterModel = updateSmartMeterModel)

        return request<UpdateSmartMeterModel, OkDto>(
            localVariableConfig
        )
    }

    /**
    * To obtain the request config of the operation smartMeterUpdatePut
    *
    * @param updateSmartMeterModel  (optional)
    * @return RequestConfig
    */
    fun smartMeterUpdatePutRequestConfig(updateSmartMeterModel: UpdateSmartMeterModel?) : RequestConfig<UpdateSmartMeterModel> {
        val localVariableBody = updateSmartMeterModel
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        localVariableHeaders["Content-Type"] = "application/json"
        localVariableHeaders["Accept"] = "application/json"

        return RequestConfig(
            method = RequestMethod.PUT,
            path = "/SmartMeter/Update",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )
    }

}
