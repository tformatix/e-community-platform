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
import at.fhooe.ecommunity.util.ECommunityFormatter
import java.util.UUID

/**
 * is displayed when a smart meter does not meet the requirements
 * @param monitoringStatus monitoring status where the smart meter does not meet the requirements
 * @param onToggleMute handler for toggling the muting of the non-compliance messages for the current hour
 * @see Composable
 */
@Composable
fun ECommunityNonCompliance(monitoringStatus: MonitoringStatusDto, onToggleMute: (smartMeterId: UUID) -> Unit) {
    val formatter = ECommunityFormatter(LocalContext.current)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)) // round shape
            .background(colorResource(id = R.color.gray_light))
            .padding(8.dp)
    ) {
        // headline
        Text(
            text = stringResource(R.string.e_community_non_compliance_title),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
        )
        // sub-headline (smart meter name)
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
            // tile with forecast and flexibility
            ECommunityTile(
                title = stringResource(R.string.e_community_non_compliance_forecast),
                content = formatter.formatSmartMeterValue(
                    monitoringStatus.forecast ?: 0,
                    true
                ),
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            // tile with projected consumption
            ECommunityTile(
                title = stringResource(R.string.e_community_non_compliance_current),
                content = formatter.formatSmartMeterValue(
                    monitoringStatus.projectedActiveEnergyPlus ?: 0,
                    true
                ),
                color = colorResource(id = R.color.value_bad),
                icon = Icons.Outlined.WarningAmber, // warning icon
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }

        // toggle muting of non-compliance
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable {
                    monitoringStatus.smartMeterId?.let {
                        onToggleMute(it)
                    }
                },
        ) {
            val icon : ImageVector
            val tint: Color
            val textId: Int
            if (monitoringStatus.isNonComplianceMuted == true){
                icon = Icons.Outlined.Notifications
                tint = colorResource(id = R.color.value_good)
                textId = R.string.e_community_non_compliance_unmute
            }else{
                icon = Icons.Outlined.NotificationsOff
                tint = colorResource(id = R.color.value_bad)
                textId = R.string.e_community_non_compliance_mute
            }
            Text(
                text = stringResource(id = textId),
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

    ECommunityDivider() // horizontal line
}