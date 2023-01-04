package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MonitoringStatusDto
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile
import at.fhooe.ecommunity.util.Formatter

@Composable
fun ECommunityNonCompliance(monitoringStatus: MonitoringStatusDto) {
    val formatter = Formatter(LocalContext.current)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.gray_light))
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.e_community_non_compliance_title),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        Text(
            text = monitoringStatus.smartMeterName ?: "",
            fontSize = 10.sp,
            color = Color.Black,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ECommunityTile(
                title = stringResource(R.string.e_community_non_compliance_forecast),
                content = formatter.formatSmartMeterValue(
                    monitoringStatus.estimatedActiveEnergyPlus ?: 0,
                    true
                ),
                subContent = "(+0.3 kWh)",
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            ECommunityTile(
                title = stringResource(R.string.e_community_non_compliance_current),
                content = formatter.formatSmartMeterValue(
                    monitoringStatus.projectedActiveEnergyPlus ?: 0,
                    true
                ),
                color = colorResource(id = R.color.value_bad),
                icon = Icons.Outlined.WarningAmber,
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable {
                    // TODO
                },
        ) {
            val icon : ImageVector
            val tint: Color
            if (monitoringStatus.isNonComplianceMuted == true){
                icon = Icons.Outlined.NotificationsOff
                tint = colorResource(id = R.color.value_bad)
            }else{
                icon = Icons.Outlined.Notifications
                tint = colorResource(id = R.color.value_good)
            }
            Text(
                text = stringResource(id = R.string.e_community_non_compliance_mute),
                fontSize = 10.sp,
                color = Color.Black,
            )
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.e_community_non_compliance_mute_icon_desc),
                tint = tint,
            )
        }
    }

    ECommunityDivider()
}