package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.WarningAmber
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
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile

@Composable
fun ECommunityNonCompliance() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(id = R.color.gray_light))
            .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
            ) {
                Text(
                    text = stringResource(R.string.e_community_non_compliance_title),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Fischer's House 1",
                    fontSize = 10.sp,
                )
            }
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.e_community_non_compliance_close_icon_desc),
                    tint = colorResource(id = R.color.value_bad),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ECommunityTile(
                title = stringResource(R.string.e_community_non_compliance_forecast),
                content = "2.3 kWh",
                subContent = "(+0.3 kWh)",
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            ECommunityTile(
                title = stringResource(R.string.e_community_non_compliance_current),
                content = "3.1 kWh",
                color = colorResource(id = R.color.value_bad),
                icon = Icons.Outlined.WarningAmber,
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }

    ECommunityDivider()
}