package at.fhooe.smartmeter.ui.home

import at.fhooe.smartmeter.models.LocalDeviceConfig
import at.fhooe.smartmeter.util.ActiveDataMode
import local.org.openapitools.client.models.SmartMeterModel

object HomeConfiguration {
    var mActiveDataMode: ActiveDataMode? = null

    fun setActiveDataMode(activeDataMode: ActiveDataMode) {
        this.mActiveDataMode = activeDataMode
    }

    fun getActiveDataMode(): ActiveDataMode {
        if (mActiveDataMode != null) {
            return mActiveDataMode as ActiveDataMode
        }

        return ActiveDataMode.REALTIME
    }
}