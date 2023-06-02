package at.fhooe.smartmeter.util

object Constants {
    /* SharedPreferences */
    const val SHARED_PREFS_FILE = "SMART_METER_PREFS"
    const val SHARED_PREFS_KEY = "x4!=KLA3CV#"

    /* WIFI */
    const val WIFI_PW_MIN_LENGTH = 8

    /* NSD Manager */
    const val NSD_SERVICE_TYPE = "_workstation._tcp."
    const val STATE_STARTED = 0
    const val STATE_RUNNING = 1

    /* Bundles */
    const val BUNDLE_LOCAL_DEVICE_NAME = "local_device_name"
    const val BUNDLE_LOCAL_DEVICE_IP = "local_device_ip"
    const val BUNDLE_WIFI_SSID = "wifi_ssid"

    /* Show Fragments */
    const val SHOW_STARTUP_ACTIVITY = 0
    const val SHOW_REGISTER = 1
    const val SHOW_MAIN_ACTIVITY = 2
    const val SHOW_PASSWORD_FORGOTTEN = 3
    const val SHOW_LOGIN = 4
    const val SHOW_LOCAL_DISCOVERY = 5
    const val SHOW_LOCAL_ADD_NETWORK = 6
    const val SHOW_LOCAL_CONNECT_WIFI = 7
    const val SHOW_LOCAL_SUMMARY = 8
    const val SHOW_LOCAL_DEVICE_SETTINGS = 9
    const val SHOW_HOME = 10
    const val SHOW_REGISTER_CONFIRMATION = 11
    const val SHOW_LOADING_SCREEN = 12

    /* Tutorial */
    const val TUTORIAL_NEWS_FINISHED = 1
    const val TUTORIAL_E_COM_FINISHED = 2
    const val TUTORIAL_HOME_FINISHED = 3
    const val TUTORIAL_USER_FINISHED = 4
    const val TUTORIAL_ACTIONBAR_PREVIEW = 0
    const val TUTORIAL_ACTIONBAR_INFO = 1
    const val TUTORIAL_ACTIONBAR_SKIP = 2

    /* IDs */
    const val MAIN_NAV_NEWS = 0
    const val MAIN_NAV_HOME = 1
    const val MAIN_NAV_E_COM = 2
    const val MAIN_NAV_USER = 3

    /* HTTP */
    const val HTTP_BASE_URL_LOCAL = "http://localhost" // base url
    const val HTTP_BASE_URL_CLOUD = "https://e-community.azurewebsites.net/"
    const val HTTP_AUTHORIZATION_KEY: String = "Authorization"
    const val HTTP_AUTHORIZATION_PREFIX: String = "Bearer"
    const val HTTP_LOCAL_PORT = 5001

    /* SIGNALR */
    const val SIGNALR_URL = "Endpoint/EndDevice"
    const val SIGNALR_METHOD = "ReceiveRTData"

    /* ACTIVE DATA MODE */
    const val ACTIVE_DATA_MODE_HOME_RT = 100
    const val ACTIVE_DATA_MODE_HOME_HIST = 101

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
    const val CHECK_SIGNALR_TIMER = 10000 /* 10sec */
    const val CHECK_CONNECT_TIMER = 7000 /* 7sec */
}