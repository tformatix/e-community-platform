package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
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
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Forecast was not met!",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_close),
                contentDescription = "Close",
                tint = colorResource(id = R.color.value_bad),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            ECommunityTile(
                title = "forecast",
                content = "2.3 kWh",
                subContent = "(+0.3 kWh)",
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            ECommunityTile(
                title = "current projected consumption",
                content = "3.1 kWh",
                color = colorResource(id = R.color.value_bad),
                icon = ImageVector.vectorResource(id = R.drawable.ic_warning),
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }

    ECommunityDivider()
}