package at.fhooe.smartmeter.models

import org.openapitools.client.models.MinimalSmartMeterDto

data class Settings(
    var smartMeterList: List<MinimalSmartMeterDto>
)
