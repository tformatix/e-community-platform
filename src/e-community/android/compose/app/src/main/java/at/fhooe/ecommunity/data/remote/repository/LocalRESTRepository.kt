package at.fhooe.ecommunity.data.remote.repository

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.compose.runtime.MutableState
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.TAG
import at.fhooe.ecommunity.model.Local
import java.net.Inet4Address
import java.net.UnknownHostException

/**
 * communicate with the raspberry
 * starts a mDNS discovery of devices in the local network
 * @see mContext used for NSD_Service
 */
class LocalRESTRepository(private val mContext: Context) {

    private var mNsdManager: NsdManager = mContext.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var mDiscoveryListener: NsdManager.DiscoveryListener? = null
    private var mResolveListener: NsdManager.ResolveListener? = null
    private lateinit var mService: NsdServiceInfo

    /**
     * start the discovery service and try to find a local device
     * @param _localDevice state of the discovered device
     */
    fun discover(_localDevice: MutableState<Local>) {
        initializeResolveListener(_localDevice)
        initializeDiscoveryListener(_localDevice)

        mNsdManager.discoverServices(
            Constants.NSD_SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            mDiscoveryListener
        )
    }

    /**
     * initialize the discovery listener
     * @param _localDevice of the discovered device
     */
    private fun initializeDiscoveryListener(_localDevice: MutableState<Local>) {
        mDiscoveryListener = object : NsdManager.DiscoveryListener {
            // start discovery
            override fun onDiscoveryStarted(regType: String) {
                Log.d(TAG, "Service discovery started")
            }

            // service got found => resolve it
            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(TAG, "Service discovery success$service")
                mNsdManager.resolveService(service, mResolveListener)
                mResolveListener = null
                initializeResolveListener(_localDevice)
            }

            // service got lost
            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e(TAG, "service lost: $service")
            }

            // discovery stopped
            override fun onDiscoveryStopped(serviceType: String) {
                Log.i(TAG, "Discovery stopped: $serviceType")
            }

            // start discovery failed => stop service
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                mNsdManager.stopServiceDiscovery(this)
            }

            // start discovery failed => stop service
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                mNsdManager.stopServiceDiscovery(this)
            }
        }
    }

    /**
     * initialize resolve listener
     * @param _localDevice of the discovered device
     */
    private fun initializeResolveListener(_localDevice: MutableState<Local>) {
        mResolveListener = object : NsdManager.ResolveListener {

            // resolve failed
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            /**
             * a service found, now resolve port/ip
             * @param serviceInfo info of the discovered service
             */
            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {

                mService = serviceInfo
                val port = mService.port
                val host = mService.host

                val addressBytes = host.address
                try {
                    val dest = Inet4Address.getByAddress(addressBytes) as Inet4Address
                    Log.d(TAG, "found connection to ${dest.hostAddress}")

                    val hostname = serviceInfo.serviceName.split('[')[0].trim()

                    // change value of state
                    _localDevice.value = Local(hostname, dest.hostAddress)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
