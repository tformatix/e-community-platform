package at.fhooe.smartmeter.api

interface VolleyRequest<T> {
    /**
     * method for sending requests to the backend
     * */
    fun sendVolleyRequest(requestType: Int, requestObject: T?) {}
}