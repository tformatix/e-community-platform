package at.fhooe.smartmeter.data

import android.util.Log
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.WifiNetwork
import at.fhooe.smartmeter.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import local.org.openapitools.client.apis.PairingApi
import local.org.openapitools.client.models.NetworkConnectModel

class PairingRepository {

    /**
     * call backend for available wifi network for this localDevice
     * @see Local
     * @see WifiNetwork
     */
    suspend fun getAvailableNetworks(localDevice: Local): Flow<List<WifiNetwork>> {
        val pairingApi = PairingApi("http://${localDevice.ipAddress}:${Constants.HTTP_LOCAL_PORT}")

        try {
            val availableNetworks = pairingApi.pairingNetworkDiscoveryGet()

            val wifiNetworks: Flow<List<WifiNetwork>> = flow {
                val wifiNetworks: MutableList<WifiNetwork> = emptyList<WifiNetwork>().toMutableList()

                for (i in availableNetworks) {
                    wifiNetworks.add(WifiNetwork(i.ssid, i.quality?.toInt()))
                }

                emit(wifiNetworks)
            }

            return wifiNetworks
        }
        catch (e: Exception) {
            val wifiNetworks: Flow<List<WifiNetwork>> = flow {
                val wifiNetworks: MutableList<WifiNetwork> = emptyList<WifiNetwork>().toMutableList()

                emit(wifiNetworks)
            }

            return wifiNetworks
        }
    }

    fun connectToWifiNetwork(localDevice: Local, wifiNetwork: WifiNetwork, password: String) {
        val pairingApi = PairingApi("http://${localDevice.ipAddress}:${Constants.HTTP_LOCAL_PORT}")

        val networkConnectModel = NetworkConnectModel(wifiNetwork.SSID, password)
        val result = pairingApi.pairingNetworkAddPost(networkConnectModel)
    }
}