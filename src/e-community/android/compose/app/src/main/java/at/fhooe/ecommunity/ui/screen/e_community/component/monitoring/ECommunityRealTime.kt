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
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile
import at.fhooe.ecommunity.util.ECommunityFormatter
import java.util.*

/**
 * displays the real time consumption of the smart meter
 * @param meterDataRT real time data
 * @param selectedSmartMeterId id of selected smart meter
 * @see Composable
 */
@Composable
fun ECommunityRealTime(meterDataRT: BufferedMeterDataRTDto?, selectedSmartMeterId: UUID?) {
    val formatter = ECommunityFormatter(LocalContext.current)

    // default value
    var currentConsumptionString = "-"
    if(meterDataRT != null){
        // if real time data is not null --> replace default value
        val meterData = meterDataRT.meterDataMember?.find { it.smartMeterId == selectedSmartMeterId } // find meter data of smart meter
        if(meterData != null){
            // meter data available
            currentConsumptionString = formatter.formatSmartMeterValue(meterData.activePowerPlus)
        }
    }

    // headline
    Text(
        text = stringResource(R.string.e_community_real_time_title),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    )

    // tile for consumption
    ECommunityTile(
        title = stringResource(R.string.e_community_real_time_consumption),
        content = currentConsumptionString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
    )
}