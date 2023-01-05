package at.fhooe.ecommunity.ui.screen.e_community.component.monitoring

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalSmartMeterDto
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile
import at.fhooe.ecommunity.util.Formatter
import java.util.*

@Composable
fun ECommunityRealTime(meterDataRT: BufferedMeterDataRTDto?, selectedSmartMeterId: UUID?) {
    val formatter = Formatter(LocalContext.current)

    var currentConsumptionString = "-"
    if(meterDataRT != null){
        val meterData = meterDataRT.meterDataMember?.find { it.smartMeterId == selectedSmartMeterId }
        if(meterData != null){
            currentConsumptionString = formatter.formatSmartMeterValue(meterData.activePowerPlus)
        }
    }

    Text(
        text = stringResource(R.string.e_community_real_time_title),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    )

    ECommunityTile(
        title = stringResource(R.string.e_community_real_time_consumption),
        content = currentConsumptionString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
    )
}