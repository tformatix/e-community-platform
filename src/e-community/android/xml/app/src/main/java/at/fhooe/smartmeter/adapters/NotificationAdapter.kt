package at.fhooe.smartmeter.adapters

import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.models.Notification

class NotificationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mTag = "NotificationAdapter"
    private var mNotifications: List<Notification> = emptyList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mNotificationTimestamp: TextView = view.findViewById<TextView>(R.id.notification_item_timestamp)
        val mNotificationDescription: TextView = view.findViewById<TextView>(R.id.notification_item_description)
    }

    class ViewHolderHeadliner(view: View) : RecyclerView.ViewHolder(view) {
        val mNotificationHeadlineTimestamp: TextView = view.findViewById<TextView>(R.id.notification_headline_item_timestamp)
        val mNotificationHeadlineDescription: TextView = view.findViewById<TextView>(R.id.notification_headline_item_description)
    }

    /**
     * update notifications
     */
    fun setNotifications(notifications: List<Notification>) {
        mNotifications = notifications.sortedBy { it.notificationId }

        Log.d(mTag, "notifications has changed size: ${mNotifications.count()}")

        for (i in 1..mNotifications.count()) {
            notifyItemChanged(i)
        }

        notifyDataSetChanged()
    }

    /**
     * sets the viewHolder based on the viewType
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when(viewType) {
            R.layout.notification_headline_item -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.notification_headline_item, parent, false)

                ViewHolderHeadliner(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.notification_item, parent, false)

                ViewHolder(view)
            }
        }
    }

    /**
     * returns count of all notifications
     */
    override fun getItemCount(): Int {
        return mNotifications.count()
    }

    /**
     * first position is the headline
     * others have the normal layout
     */
    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            R.layout.notification_headline_item
        } else {
            R.layout.notification_item
        }
    }

    /**
     * change holder based on itemViewTyp
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notification = mNotifications[position]

        when(holder.itemViewType) {
            R.layout.notification_headline_item -> {
                val viewHolderHeadliner = holder as ViewHolderHeadliner

                viewHolderHeadliner.mNotificationHeadlineTimestamp.text = notification.timestamp
                viewHolderHeadliner.mNotificationHeadlineDescription.text = notification.description
            }

            R.layout.notification_item -> {
                val viewHolder = holder as ViewHolder

                // convert to html for text formation
                val description: Spanned = Html.fromHtml(notification.description, FROM_HTML_MODE_LEGACY)

                viewHolder.mNotificationDescription.text = description
                viewHolder.mNotificationTimestamp.text = notification.timestamp

                // set timestamp in tutorial notifications to visibility gone
                if (notification.notificationId.contains("tutorial")) {
                    viewHolder.mNotificationTimestamp.visibility = View.GONE
                }
                else {
                    viewHolder.mNotificationTimestamp.visibility = View.VISIBLE
                }
            }
        }
    }

}