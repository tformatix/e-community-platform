package at.fhooe.smartmeter.services

interface SignalRManager {

    /**
     * initialize SignalRListener and request RT data
     */
    fun initSignalR()

    /**
     * tries to start the connection
     */
    fun startSignalR(accessToken: String)

    /**
     * stop signal R
     */
    fun stopSignalR()

    /**
     * receive RT data
     */
    fun receiveData(data: Any)
}