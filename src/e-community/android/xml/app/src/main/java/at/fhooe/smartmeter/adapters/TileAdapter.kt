package at.fhooe.smartmeter.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.TAG
import at.fhooe.smartmeter.database.TileViewModel
import at.fhooe.smartmeter.models.Tile
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class TileAdapter(tileViewModel: TileViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), TileMoveCallback.TileTouchHelperContract {

    private var mTag = "TileAdapter"
    private var mTiles = emptyList<Tile>()
    private var mTileViewModel = tileViewModel
    private lateinit var mContext: Context
    private lateinit var mTileDragAndDropListener: TileDragAndDropListener
    private val mAnimateValue = AlphaAnimation(1.0f, 0.0f)


    init {
        mAnimateValue.duration = 150
        mAnimateValue.repeatCount = 1
        mAnimateValue.repeatMode = Animation.REVERSE
    }

    class ViewHolderSmall(view: View): RecyclerView.ViewHolder(view) {
        var mTileRegion: TextView = view.findViewById<TextView>(R.id.tile_small_region)
        var mTileAction: TextView = view.findViewById<TextView>(R.id.tile_small_action)
        var mTileValue: TextView = view.findViewById<TextView>(R.id.tile_small_value)
        var mRowView = view
    }

    class ViewHolderLarge(view: View): RecyclerView.ViewHolder(view) {
        var mTileRegion: TextView = view.findViewById<TextView>(R.id.tile_large_region)
        var mTileAction: TextView = view.findViewById<TextView>(R.id.tile_large_action)
        var mTileValue: TextView = view.findViewById<TextView>(R.id.tile_large_value)
    }

    /**
     * update tiles
     */
    fun setTiles(tiles: List<Tile>) {
        mTiles = tiles.sortedBy { it.order }

        for (i in 0 until mTiles.count()) {
            notifyItemChanged(i)
        }

        notifyDataSetChanged()
    }

    /**
     * return viewHolder (maybe we will have multiple here sometime)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mContext = parent.context

        return when(viewType) {
            R.layout.tile_small -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tile_small, parent, false)

                ViewHolderSmall(view)
            }
            else /*R.layout.tile_large */ -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tile_large, parent, false)

                ViewHolderLarge(view)
            }
        }
    }

    /**
     * layout contains small and large tiles
     */
    override fun getItemViewType(position: Int): Int {
        return if (mTiles[position].isLargeTile) {
            R.layout.tile_large
        } else {
            R.layout.tile_small
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tile = mTiles[position]

        val viewHolder: RecyclerView.ViewHolder

        when (holder.itemViewType) {
            R.layout.tile_small -> {
                viewHolder = holder as ViewHolderSmall

                viewHolder.mTileRegion.text = tile.region
                viewHolder.mTileAction.text = tile.action

                // animate tile when value changes
                if (viewHolder.mTileValue.text != tile.value) {
                    viewHolder.mTileValue.startAnimation(mAnimateValue)
                }

                viewHolder.mTileValue.text = tile.value

                viewHolder.mTileValue.setTextColor(tile.colorId)
            }

            R.layout.tile_large -> {
                viewHolder = holder as ViewHolderLarge

                viewHolder.mTileRegion.text = tile.region
                viewHolder.mTileAction.text = tile.action
                viewHolder.mTileValue.text = tile.value

                viewHolder.mTileValue.setTextColor(tile.colorId)
            }
        }

    }

    /**
     * get amount of tiles
     */
    override fun getItemCount(): Int {
        return mTiles.size
    }

    fun setTileDragAndDropListener(listener: TileDragAndDropListener) {
        mTileDragAndDropListener = listener
    }

    /**
     * interface for interacting with the tile drag and drop workflow
     */
    interface TileDragAndDropListener {

        /**
         * drag has started; stop updating value till drag is finished
         */
        fun dragStarted()

        /**
         * drag has finished;
         */
        fun dragFinished()
    }

    /**
     * swap order of the dragged/dropped tiles
     */
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "swap: ${mTiles[fromPosition].tileId} $fromPosition with ${mTiles[toPosition].tileId} $toPosition")
        mTiles[fromPosition].order = toPosition
        mTiles[toPosition].order = fromPosition

        val diffTiles = abs(fromPosition-toPosition)
        if (diffTiles == 2) {
            mTiles[fromPosition].order = diffTiles
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(tileViewHolder: ViewHolderSmall) {
        mTileDragAndDropListener.dragStarted()
        tileViewHolder.mRowView.setBackgroundColor(Color.GRAY)
    }

    override fun onRowClear(tileViewHolder: ViewHolderSmall) {

        mTiles = mTiles.sortedBy { it.order }

        Log.d(TAG, mTiles.toString())
        // todo michi 15.02.2022: update order in room db
        CoroutineScope(Dispatchers.Default).launch {
            mTileViewModel.update(mTiles)

            CoroutineScope(Dispatchers.Main).launch {
                notifyDataSetChanged()
            }
        }

        tileViewHolder.mRowView.background = AppCompatResources.getDrawable(mContext, R.drawable.notification_background)

        mTileDragAndDropListener.dragFinished()
    }
}