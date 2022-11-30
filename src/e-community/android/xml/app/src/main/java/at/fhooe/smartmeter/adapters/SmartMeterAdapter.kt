package at.fhooe.smartmeter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.SmartMeter
import at.fhooe.smartmeter.ui.profile.local.LocalDiscoveryFragment
import org.openapitools.client.models.MinimalSmartMeterDto

class SmartMeterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSmartMeters: MutableList<MinimalSmartMeterDto> = emptyList<MinimalSmartMeterDto>().toMutableList()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var mSmartMeterName = view.findViewById<TextView>(R.id.smartmeter_item_name)
        var mSmartMeterDesc = view.findViewById<TextView>(R.id.smartmeter_item_description)
    }

    fun setSmartMeters(smartMeters: List<MinimalSmartMeterDto>) {
        mSmartMeters = smartMeters.toMutableList()

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.smartmeter_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val smartMeter = mSmartMeters[position]
        val viewHolder = holder as ViewHolder

        with(viewHolder) {
            mSmartMeterName.text = smartMeter.name
            mSmartMeterDesc.text = smartMeter.description
        }
    }

    /**
     * get count of smart_meter devices
     */
    override fun getItemCount(): Int {
        return mSmartMeters.size
    }
}