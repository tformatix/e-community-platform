package at.fhooe.ecommunity

/**
 * all constants of the application
 */
object Constants {
    /* SHARED PREFERENCES */
    const val SHARED_PREFS_FILE = "SMART_METER_PREFS"

    const val BROADCAST_RECEIVER_NOTIFICATION = "BROADCAST_RECEIVER_NOTIFICATION"
    const val BROADCAST_RECEIVER_EXTRA_MESSAGE = "EXTRA_MESSAGE"
    const val BROADCAST_RECEIVER_EXTRA_BADGE = "EXTRA_BADGE"

    /* ENCRYPTED SHARED PREFERENCES */
    const val ENCRYPTED_KEY_ALIAS = "_androidx_security_master_key_"
    const val ENCRYPTED_KEY_SIZE = 256

    /* WIFI */
    const val WIFI_PW_MIN_LENGTH = 8

    /* NSD Manager */
    const val NSD_SERVICE_TYPE = "_workstation._tcp."

    /* HTTP */
    const val HTTP_BASE_URL_CLOUD = "https://e-community.azurewebsites.net/"
    const val HTTP_AUTHORIZATION_KEY: String = "Authorization"
    const val HTTP_AUTHORIZATION_PREFIX: String = "Bearer"

    /* SIGNALR */
    const val SIGNALR_URL = "Endpoint/EndDevice"
    const val SIGNALR_METHOD = "ReceiveRTData"

    /* SMART METER VALUES */
    const val KILOWATT = 1000
    const val MEGAWATT = 1000000
    const val GIGAWATT = 1000000000

    const val WATT_UNIT = "W"
    const val KILOWATT_UNIT = "kW"
    const val MEGAWATT_UNIT = "MW"
    const val GIGAWATT_UNIT = "GW"
    const val TIMER_COUNT = 30
    const val EXTEND_TIMER = 300000 /* 5min */
    const val CHECK_SIGNALR_TIMER = 3000 /* 10sec */
    const val CHECK_CONNECT_TIMER = 7000 /* 7sec */

    /* STARTUP IMAGES */
    const val URL_IMAGE_1 = "https://assets-us-01.kc-usercontent.com/c8286d4d-bff3-4363-913b-3483d8372a70/3cea7075-8ce3-4383-9789-8fd49984c2f3/planetka_transparent.gif"
    const val URL_IMAGE_2 = "https://cdn.dribbble.com/users/7218414/screenshots/15271909/saving_money.gif"
    const val URL_IMAGE_3 = "https://www.300feetout.com/wp-content/uploads/2020/09/300FeetOut_Zoom-Grid.gif"

    /* NEWS-Search */
    const val VIEW_MORE_RESULTS = 3
}