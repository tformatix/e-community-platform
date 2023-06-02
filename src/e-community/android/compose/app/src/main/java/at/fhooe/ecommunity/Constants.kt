package at.fhooe.ecommunity

/**
 * all constants of the application
 */
object Constants {
    /* SHARED PREFERENCES */
    const val SHARED_PREFS_FILE = "SMART_METER_PREFS"

    /* BROADCAST RECEIVER FOR FCM */
    const val BROADCAST_RECEIVER_NOTIFICATION = "BROADCAST_RECEIVER_NOTIFICATION"
    const val BROADCAST_RECEIVER_NOTIFICATION_EXTRA_MESSAGE = "NOTIFICATION_EXTRA_MESSAGE" // intent extra
    const val BROADCAST_RECEIVER_NOTIFICATION_EXTRA_ID = "NOTIFICATION_EXTRA_ID" // intent extra

    /* ID OF E_COMMUNITY NOTIFICATION (used for distinction  of notifications) */
    const val NOTIFICATION_ID_E_COMMUNITY = "e_community"

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
    const val SIGNALR_METHOD_BLOCK_ACC_BALANCE = "ReceiveBlockchainAccountBalance"
    const val SIGNALR_METHOD_CREATE_CON_CONTRACT = "ReceiveCreateConsentContract"
    const val SIGNALR_METHOD_CONTRACTS_FOR_MEMBER = "ReceiveContractsForMember"
    const val SIGNALR_METHOD_HISTORY_DATA = "ReceiveHistoryData"
    const val SIGNALR_METHOD_DEPOSIT_TO_CONTRACT = "ReceiveDepositToContract"
    const val SIGNALR_METHOD_WITHDRAW_FROM_CONTRACT = "ReceiveWithdrawFromContract"

    /* SMART METER VALUES */
    const val KILOWATT = 1000
    const val MEGAWATT = 1000000
    const val GIGAWATT = 1000000000

    const val WATT_UNIT = "W"
    const val KILOWATT_UNIT = "kW"
    const val MEGAWATT_UNIT = "MW"
    const val GIGAWATT_UNIT = "GW"
    const val HOUR_UNIT = "h"
    const val TIMER_COUNT = 30
    const val CHECK_SIGNALR_TIMER = 3000 /* 10sec */

    /* STARTUP IMAGES */
    const val URL_IMAGE_1 = "https://assets-us-01.kc-usercontent.com/c8286d4d-bff3-4363-913b-3483d8372a70/3cea7075-8ce3-4383-9789-8fd49984c2f3/planetka_transparent.gif"
    const val URL_IMAGE_2 = "https://cdn.dribbble.com/users/7218414/screenshots/15271909/saving_money.gif"
    const val URL_IMAGE_3 = "https://www.300feetout.com/wp-content/uploads/2020/09/300FeetOut_Zoom-Grid.gif"

    /* NEWS-Search */
    const val VIEW_MORE_RESULTS = 3

    /* E-COMMUNITY */
    const val DEFAULT_PERFORMANCE_DURATION_DAYS = 1 // default duration days for performance
    const val MAX_MEMBERS_TOP_BAR = 4 // maximum amount of members showed on the top bar (if more --> "...")

    /* CONTRACT */
    const val CONTRACT_NEW = -1
    const val CONTRACT_ACTIVE = 0
    const val CONTRACT_REQUESTS = 1
    const val CONTRACT_PENDING = 2
    const val CONTRACT_CLOSED = 3
}