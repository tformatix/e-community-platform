package at.fhooe.smartmeter.util

import android.content.Context
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.database.TileViewModel
import at.fhooe.smartmeter.models.Tile

const val DEFAULT_TILE_VALUE = "0 W"

class DashboardManager(context: Context, tileViewModel: TileViewModel) {

    var mContext = context
    var mTileViewModel = tileViewModel

    /**
     * insert default tiles into room
     */
    fun setupTilesHome() {
        val tileHouseholdConsumption = Tile (
            tileId = mContext.getString(R.string.tile_id_household_consumption),
            region = mContext.getString(R.string.tile_region_household),
            action = mContext.getString(R.string.tile_action_consumption),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 0,
            colorId = mContext.getColor(R.color.tile_value_normal))

        val tileHouseholdFeedIn = Tile (
            tileId = mContext.getString(R.string.tile_id_household_feed_in),
            region = mContext.getString(R.string.tile_region_household),
            action = mContext.getString(R.string.tile_action_feed_in),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 1,
            colorId = mContext.getColor(R.color.tile_value_good))

        val tileCommunityConsumption = Tile(
            tileId = mContext.getString(R.string.tile_id_community_consumption),
            region = mContext.getString(R.string.tile_region_community),
            action = mContext.getString(R.string.tile_action_consumption),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 2,
            colorId = mContext.getColor(R.color.tile_value_normal)
        )

        val tileCommunityFeedIn = Tile(
            tileId = mContext.getString(R.string.tile_id_community_feed_in),
            region = mContext.getString(R.string.tile_region_community),
            action = mContext.getString(R.string.tile_action_feed_in),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 3,
            colorId = mContext.getColor(R.color.tile_value_good)
        )

        val list = listOf(tileHouseholdConsumption, tileHouseholdFeedIn, tileCommunityConsumption, tileCommunityFeedIn)
        mTileViewModel.insert(list)
    }
}