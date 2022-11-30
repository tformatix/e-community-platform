package at.fhooe.smartmeter.ui.startup


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import at.fhooe.smartmeter.R
import com.bumptech.glide.Glide
import java.util.*


class WelcomeViewPagerAdapter(context: Context, images: Array<Int>) : PagerAdapter() {

    private val mContext = context
    private val mImages = images
    private val mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return mImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.viewpager_welcome_item, container, false)

        val imageView = itemView.findViewById<ImageView>(R.id.viewpager_welcome_item_image)

        Glide.with(mContext).asGif().load(mImages[position]).into(imageView)

        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }


}