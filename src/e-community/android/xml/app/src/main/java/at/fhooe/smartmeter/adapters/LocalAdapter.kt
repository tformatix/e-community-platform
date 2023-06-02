package at.fhooe.smartmeter.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.ui.profile.local.LocalDiscoveryFragment
import org.openapitools.client.apis.AuthApi

class LocalAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mLocals: MutableList<Local> = emptyList<Local>().toMutableList()

    private var mLocalConnectionListener: LocalConnectionListener? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var mLocalDeviceName: TextView = view.findViewById(R.id.local_item_device_name)
        var mLocalIpAddress: TextView = view.findViewById(R.id.local_item_ip)
        val mLocalDeviceConnect: Button = view.findViewById(R.id.local_item_connect)
    }

    fun setLocals(locals: List<Local>) {
        for (i in locals) {
            if (!mLocals.contains(i)) {
                mLocals.add(i)
            }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.local_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val local = mLocals[position]
        val viewHolder = holder as ViewHolder

        with(viewHolder) {
            mLocalDeviceName.text = local.deviceName
            mLocalIpAddress.text = local.ipAddress
            mLocalDeviceConnect.setOnClickListener {
                mLocalConnectionListener?.onConnect(local)
            }
        }
    }

    /**
     * set the listener from LocalDiscoveryFragment
     * @see LocalDiscoveryFragment
     * @see LocalConnectionListener
     */
    fun setLocalConnectionListener(localConnectionListener: LocalConnectionListener) {
        this.mLocalConnectionListener = localConnectionListener
    }

    /**
     * get count of local devices
     */
    override fun getItemCount(): Int {
        return mLocals.size
    }

    /**
     * interface for communication with LocalDiscoveryFragment
     * @see LocalDiscoveryFragment
     */
    interface LocalConnectionListener {

        // connect to selected local device
        fun onConnect(local: Local)
    }
}