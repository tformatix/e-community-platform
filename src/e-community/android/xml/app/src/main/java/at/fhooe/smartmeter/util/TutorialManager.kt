package at.fhooe.smartmeter.util

import android.content.Context
import android.util.Log
import at.fhooe.smartmeter.R
import at.fhooe.smartmeter.database.NotificationViewModel
import at.fhooe.smartmeter.database.TileViewModel
import at.fhooe.smartmeter.models.Notification
import at.fhooe.smartmeter.models.Tile

class TutorialManager(context: Context) {

    private var mContext = context
    private var mTag = "TutorialManager"

    init {

    }

    /**
     * setups up the tutorial
     * set shared_preferences
     */
    fun setup() {
        Log.d(mTag, "setup tutorial")

        val encryptedPreferences = EncryptedPreferences(mContext)

        // set tutorial mode active
        encryptedPreferences.sharedPreferences
            ?.edit()
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_mode), true)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_home_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_dash_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_e_com_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_user_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_loaded), false)
            ?.apply()
    }

    /**
     * clears the tutorial settings (normal login)
     */
    fun clear() {
        val encryptedPreferences = EncryptedPreferences(mContext)

        // set tutorial mode active
        encryptedPreferences.sharedPreferences
            ?.edit()
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_mode), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_home_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_dash_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_e_com_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_user_finished), false)
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_loaded), false)
            ?.apply()
    }

    /**
     * inserts the template notifications for the tutorial
     */
    fun prepareTutorialNotifications(notificationViewModel: NotificationViewModel) {
        Log.d(mTag, "prepare tutorial notifications")

        notificationViewModel.deleteTutorialNotifications()

        val notificationGoodDay = Notification (
                    notificationId = "tutorial_1",
                    timestamp = mContext.getString(R.string.notification_headline),
                    description = mContext.getString(R.string.notification_headline_text),
                    isRead = false,
                    priority = 10,
                    isForTutorial = true)

        val notificationFunction = Notification (
            notificationId = "tutorial_2",
            timestamp = mContext.getString(R.string.tutorial_notification_headline_good_day),
            description = mContext.getString(R.string.tutorial_notification_good_day),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationFunction2 = Notification (
            notificationId = "tutorial_3",
            timestamp = "Functions",
            description = mContext.getString(R.string.tutorial_notification_function),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationHome = Notification (
            notificationId = "tutorial_4",
            timestamp = "Home",
            description = mContext.getString(R.string.tutorial_notification_news),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationMenu = Notification (
            notificationId = "tutorial_5",
            timestamp = "Menu",
            description = mContext.getString(R.string.tutorial_notification_menu),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationHeadline = Notification (
            notificationId = "tutorial_6",
            timestamp = mContext.getString(R.string.notification_headline),
            description = mContext.getString(R.string.tutorial_preview_notification_headline),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationLowFeedIn = Notification (
            notificationId = "z_preview_1",
            timestamp = "14:10",
            description = mContext.getString(R.string.tutorial_preview_notification_low_feed_in),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationHighFeedIn = Notification (
            notificationId = "z_preview_2",
            timestamp = "19:02",
            description = mContext.getString(R.string.tutorial_preview_notification_high_feed_in),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationDailyUsage = Notification (
            notificationId = "z_preview_3",
            timestamp = "08:45",
            description = mContext.getString(R.string.tutorial_preview_notification_daily_usaged),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        val notificationECommHappy = Notification (
            notificationId = "z_preview_4",
            timestamp = "21:37",
            description = mContext.getString(R.string.tutorial_preview_notification_e_comm_happy),
            isRead = false,
            priority = 10,
            isForTutorial = true)

        // mark the tutorial notifications as loaded
        val encryptedPreferences = EncryptedPreferences(mContext)
        encryptedPreferences.sharedPreferences
            ?.edit()
            ?.putBoolean(mContext.getString(R.string.shared_prefs_tutorial_loaded), true)
            ?.apply()

        val list = listOf(notificationGoodDay,
                          notificationFunction,
                          notificationFunction2,
                          notificationHome,
                          notificationMenu,
                          notificationHeadline,
                          notificationLowFeedIn,
                          notificationHighFeedIn,
                          notificationDailyUsage,
                          notificationECommHappy)

        list.let { notificationViewModel.insertBulk(it) }
    }

    fun loadTiles(mTileViewModel: TileViewModel) {
        val tileHouseholdConsumption = Tile (
            tileId = "household_consumption",
            region = "Household",
            action = "0",
            value = "143 W",
            isVisible = true,
            order = 0,
            colorId = mContext.getColor(R.color.tile_value_normal))

        val tileHouseholdFeed = Tile (
            tileId = "household_feed",
            region = "Household",
            action = "1",
            value = "23 W",
            isVisible = true,
            order = 1,
            colorId = mContext.getColor(R.color.tile_value_good))

        val tileECommConsumption = Tile (
            tileId = "3_e_com_consumption",
            region = "E-Community",
            action = "2",
            value = "4 W",
            isVisible = true,
            order = 2,
            colorId = mContext.getColor(R.color.tile_value_bad))

        val tileECommConsumption2 = Tile (
            tileId = "3_2e_com_consumption",
            region = "E-Community",
            action = "3",
            value = "4 W",
            isVisible = true,
            order = 3,
            colorId = mContext.getColor(R.color.tile_value_normal))

        val tileECommConsumption3 = Tile (
            tileId = "3_3e_com_consumption",
            region = "E-Community",
            action = "4",
            value = "4 W",
            isVisible = true,
            order = 4,
            isLargeTile = false,
            colorId = mContext.getColor(R.color.tile_value_good))

        val list = listOf(tileHouseholdFeed, tileHouseholdConsumption, tileECommConsumption,tileECommConsumption2, tileECommConsumption3)
        mTileViewModel.insert(list)
    }
}