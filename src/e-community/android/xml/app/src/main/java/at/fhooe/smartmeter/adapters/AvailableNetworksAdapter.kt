package at.fhooe.smartmeter.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.dto.NetworkDiscoveryDto
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.WifiNetwork

class AvailableNetworksAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mAvailableNetworks: MutableList<WifiNetwork> = emptyList<WifiNetwork>().toMutableList()
    private var mWifiConnectListener: WifiConnectListener? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mSSID: TextView = view.findViewById(R.id.available_network_item_ssid)
        val mQuality: TextView = view.findViewById(R.id.available_network_item_quality)
    }

    /**
     * set available wifi networks list got from backend
     * @see List
     * @see WifiNetwork
     */
    fun setAvailableNetworks(wifiNetworks: List<WifiNetwork>) {
        mAvailableNetworks = wifiNetworks.toMutableList()
        notifyDataSetChanged()
    }

    /**
     * set listener from LocalConnectionFragment
     * @see WifiConnectListener
     */
    fun setWifiConnectListener(wifiConnectListener: WifiConnectListener) {
        mWifiConnectListener = wifiConnectListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.available_network_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val network = mAvailableNetworks[position]
        val viewHolder = holder as ViewHolder

        network.SSID?.let {
            if (it.length > 15) {
                viewHolder.mSSID.text = "${network.SSID?.subSequence(0, 15)}..."
            }
            else {
                viewHolder.mSSID.text = network.SSID
            }
        }

        viewHolder.mQuality.text = network.quality.toString()

        // connect to selected wifi
        viewHolder.itemView.setOnClickListener {
            Log.d(TAG, "connect to ${viewHolder.mSSID.text}")

            mWifiConnectListener?.onConnect(network)
        }
    }

    /**
     * size of available networks
     * @return size of networks
     */
    override fun getItemCount(): Int {
        return mAvailableNetworks.size
    }

    /**
     * go back to LocalConnectionFragment and start LocalConnectWifiFragment
     */
    interface WifiConnectListener {

        /**
         * user tries to connect to this wifiNetwork
         * @see WifiNetwork
         */
        fun onConnect(wifiNetwork: WifiNetwork)
    }
}