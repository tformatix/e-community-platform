package at.fhooe.ecommunity.data.local.setup

import android.content.Context
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.local.entity.Tile
import at.fhooe.ecommunity.data.local.repository.TileRepository

const val DEFAULT_TILE_VALUE = "0 W"

/**
 * managing tiles in home/eCommunity screen
 * @param context used for strings
 * @param tileRepository repository for room db
 */
class DashboardManager(private val context: Context, private val tileRepository: TileRepository) {

    /**
     * insert default tiles into room
     */
    fun setupTilesHome() {
        val tileHouseholdConsumption = Tile (
            tileId = context.getString(R.string.dashboard_tile_id_household_consumption),
            region = context.getString(R.string.dashboard_tile_region_household),
            action = context.getString(R.string.dashboard_tile_action_consumption),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 0,
            colorId = context.getColor(R.color.value_normal))

        val tileHouseholdFeedIn = Tile (
            tileId = context.getString(R.string.dashboard_tile_id_household_feed_in),
            region = context.getString(R.string.dashboard_tile_region_household),
            action = context.getString(R.string.dashboard_tile_action_feed_in),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 1,
            colorId = context.getColor(R.color.value_good))

        val tileCommunityConsumption = Tile(
            tileId = context.getString(R.string.dashboard_tile_id_community_consumption),
            region = context.getString(R.string.dashboard_tile_region_community),
            action = context.getString(R.string.dashboard_tile_action_consumption),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 2,
            colorId = context.getColor(R.color.value_normal)
        )

        val tileCommunityFeedIn = Tile(
            tileId = context.getString(R.string.dashboard_tile_id_community_feed_in),
            region = context.getString(R.string.dashboard_tile_region_community),
            action = context.getString(R.string.dashboard_tile_action_feed_in),
            value = DEFAULT_TILE_VALUE,
            isVisible = true,
            order = 3,
            colorId = context.getColor(R.color.value_good)
        )

        val list = listOf(tileHouseholdConsumption, tileHouseholdFeedIn, tileCommunityConsumption, tileCommunityFeedIn)
        tileRepository.insert(list)
    }
}