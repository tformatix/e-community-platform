package at.fhooe.ecommunity.model

/**
 * remote backend exception
 * @param mType exception type
 * @param mCategory (optional) exception category
 */
data class RemoteException(val mType: Type, val mCategory: Category = Category.UNCATEGORIZED) {

    /**
     * string representation
     */
    override fun toString(): String {
        return "${mCategory.name}-${mType.name}"
    }

    /**
     * exception category
     */
    enum class Category {
        UNCATEGORIZED,
        CLIENT_ERROR,
        SERVER_ERROR
    }

    /**
     * exception type
     */
    enum class Type {
        // general errors
        UNAUTHORIZED,
        NOT_FOUND,
        SERVER_ERROR,
        NO_INTERNET,
        SERVER_UNREACHABLE,
        UNEXPECTED,

        // service errors (400)
        CREATE_ACCOUNT_FAILED,
        INVALID_CREDENTIALS,
        INVALID_REFRESH_TOKEN,
        COULDNT_SEND_EMAIL,
        EMAIL_NOT_CONFIRMED,
        EMAIL_CONFIRM_FAILED,
        EMAIL_ALREADY_CONFIRMED,
        ACCOUNT_LOCKED,
        EMAIL_TAKEN,
        RESET_PASSWORD_FAILED,
        CHANGE_PASSWORD_FAILED,
        NO_SMART_METER_FOUND,
        MEMBER_ID_NOT_RESOLVABLE,
        SMART_METER_NOT_TO_USER,
        PORTION_ACK_EXPIRED,
    }

}