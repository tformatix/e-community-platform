package at.fhooe.smartmeter.models

import local.org.openapitools.client.models.SmartMeterModel
import local.org.openapitools.client.models.StatusDto

object LocalDeviceConfig {
    private var mSmartMeterModel: SmartMeterModel? = null
    private var mStatus: StatusDto? = null
    private var mLocal: Local? = null
    private var mFullyConfigured: Boolean? = null
    private var mSignalRRequested: Boolean? = null

    fun setSmartMeterModel(smartMeterModel: SmartMeterModel) {
        mSmartMeterModel = smartMeterModel
    }

    fun getSmartMeterModel(): SmartMeterModel {
        if (mSmartMeterModel != null) {
            return mSmartMeterModel as SmartMeterModel
        }

        return SmartMeterModel(isMain = false, isDirectFeedIn = false, isOverflowFeedIn = false, measuresConsumption = false, measuresFeedIn = false)
    }

    fun setStatus(statusDto: StatusDto) {
        mStatus = statusDto
    }

    fun getStatus(): StatusDto {
        if (mStatus != null) {
            return mStatus as StatusDto
        }

        return StatusDto()
    }

    fun setLocal(local: Local) {
        mLocal = local
    }

    fun getLocal(): Local {
        if (mLocal != null) {
            return mLocal as Local
        }

        return Local("", "")
    }

    fun setFullyConfigured(fullyConfigured: Boolean) {
        mFullyConfigured = fullyConfigured
    }

    fun getFullyConfigured(): Boolean {
        if (mFullyConfigured == true) {
            return mFullyConfigured as Boolean
        }

        return false
    }

    fun setSignalRRequested(requested: Boolean) {
        mSignalRRequested = requested
    }

    fun getSignalRRequested(): Boolean {
        if (mSignalRRequested == true) {
            return mSignalRRequested as Boolean
        }

        return false
    }
}