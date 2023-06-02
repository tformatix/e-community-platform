package at.fhooe.smartmeter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.models.Local
import at.fhooe.smartmeter.models.Settings


class ProfileAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mViewPool = RecycledViewPool()
    private var mSettings: MutableList<Settings> = emptyList<Settings>().toMutableList()
    private lateinit var mContext: Context

    class ViewHolderSmartMeter(view: View): RecyclerView.ViewHolder(view) {
        var mRecyclerView = view.findViewById<RecyclerView>(R.id.smartmeter_list)
    }

    fun setSettings(settings: List<Settings>) {
        for (i in settings) {
            if (!mSettings.contains(i)) {
                mSettings.add(i)
            }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_smartmeter_list_item, parent, false)
        return ViewHolderSmartMeter(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder: RecyclerView.ViewHolder

        when(position) {
            //Todo michi: constants!!
            0 /* Smart Meter */ -> {
                viewHolder = holder as ViewHolderSmartMeter

                val layoutManager = LinearLayoutManager(
                    viewHolder.mRecyclerView
                        .context,
                    LinearLayoutManager.VERTICAL,
                    false
                )

                layoutManager.initialPrefetchItemCount = mSettings[0].smartMeterList.size

                val smartMeterAdapter = SmartMeterAdapter()
                smartMeterAdapter.setSmartMeters(mSettings[0].smartMeterList)

                holder.mRecyclerView.layoutManager = layoutManager
                holder.mRecyclerView.adapter = smartMeterAdapter
                holder.mRecyclerView.setRecycledViewPool(mViewPool)
                holder.mRecyclerView.setHasFixedSize(true)
            }
        }
    }

    override fun getItemCount(): Int {
        return mSettings.size
    }

}