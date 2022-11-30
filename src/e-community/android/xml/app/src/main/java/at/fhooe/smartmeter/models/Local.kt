package at.fhooe.smartmeter.models

data class Local(var deviceName: String, var ipAddress: String) {

    override fun toString(): String {
        return "{ deviceName: $deviceName ; ipAddress: $ipAddress }"
    }
}