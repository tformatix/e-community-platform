package at.fhooe.smartmeter.services

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import at.fhooe.smartmeter.util.Constants
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.models.Local
import java.net.Inet4Address
import java.net.UnknownHostException
import java.util.*


class LocalPairingManager(private val context: Context) : Observable() {

    private var mNsdManager: NsdManager = context.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var mDiscoveryListener: NsdManager.DiscoveryListener? = null
    private var mResolveListener: NsdManager.ResolveListener? = null
    private lateinit var mService: NsdServiceInfo

    private var mLocalDevices: MutableList<Local> = emptyList<Local>().toMutableList()

    init {
        initializeResolveListener()
        initializeDiscoveryListener()
    }

    /**
     * start the discovery service and try to find a local device
     */
    fun discover() {
        mNsdManager.discoverServices(
            Constants.NSD_SERVICE_TYPE,
            NsdManager.PROTOCOL_DNS_SD,
            mDiscoveryListener
        )
    }

    /**
     * initialize the discovery listener
     */
    private fun initializeDiscoveryListener() {
        mDiscoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d(TAG, "Service discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d(TAG, "Service discovery success$service")
                mNsdManager.resolveService(service, mResolveListener)
                mResolveListener = null
                initializeResolveListener()
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e(TAG, "service lost: $service")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i(TAG, "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                mNsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                mNsdManager.stopServiceDiscovery(this)
            }
        }
    }

    /**
     * initialize resolve listener
     */
    private fun initializeResolveListener() {
        mResolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            /**
             * a service found, now resolve port/ip
             */
            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.e(TAG, "Resolve Succeeded. $serviceInfo")

                mService = serviceInfo
                val port = mService.port
                val host = mService.host

                val addressBytes = host.address
                try {
                    val dest = Inet4Address.getByAddress(addressBytes) as Inet4Address
                    Log.d(TAG, "found connection to ${dest.hostAddress}")

                    val hostname = serviceInfo.serviceName.split('[')[0].trim()
                    mLocalDevices.add(Local(hostname, dest.hostAddress))

                    setChanged()
                    notifyObservers(mLocalDevices)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
