package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MonitoringStatusDto
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider

@Composable
fun ECommunityOffline(monitoringStatus: MonitoringStatusDto) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.gray_light))
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = stringResource(R.string.e_community_offline_icon_desc),
            tint = colorResource(id = R.color.value_bad),
            modifier = Modifier
                .padding(end = 8.dp)
        )
        Text(
            text = stringResource(R.string.e_community_offline_title, monitoringStatus.smartMeterName ?: ""),
            fontSize = 12.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )
    }

    ECommunityDivider()
}