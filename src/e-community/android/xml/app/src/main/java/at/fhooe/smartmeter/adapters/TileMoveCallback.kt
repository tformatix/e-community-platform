package at.fhooe.smartmeter.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TileMoveCallback(tileAdapter: TileAdapter) : ItemTouchHelper.Callback() {

    private val mTileAdapter = tileAdapter

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT, 0)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val viewHolderSmall = viewHolder as TileAdapter.ViewHolderSmall
            mTileAdapter.onRowSelected(viewHolderSmall)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        mTileAdapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        if (viewHolder is TileAdapter.ViewHolderSmall) {
            val tileViewHolder = viewHolder as TileAdapter.ViewHolderSmall
            mTileAdapter.onRowClear(tileViewHolder)
        }
    }

    interface TileTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowSelected(tileViewHolder: TileAdapter.ViewHolderSmall)
        fun onRowClear(tileViewHolder: TileAdapter.ViewHolderSmall)
    }
}