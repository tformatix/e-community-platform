package at.fhooe.ecommunity.model

/**
 * entity for a local device (raspberry pi)
 * @param deviceName name of the mDNS host (.local domain)
 * @param ipAddress ip of the local device
 */
data class Local(var deviceName: String, var ipAddress: String) {

    /**
     * string representation
     * @return info of deviceName and ipAddress
     */
    override fun toString(): String {
        return "{ deviceName: $deviceName ; ipAddress: $ipAddress }"
    }
}