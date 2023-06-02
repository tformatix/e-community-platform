package at.fhooe.ecommunity.ui.screen.e_community.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.Constants
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.MinimalECommunityDto
import at.fhooe.ecommunity.data.remote.signalr.dto.BufferedMeterDataRTDto
import at.fhooe.ecommunity.util.ECommunityFormatter

/**
 * top bar of ECommunityScreen
 * @param eCommunity eCommunity data
 * @param meterDataRT real time data
 * @see Composable
 */
@Composable
fun ECommunityTopBar(eCommunity: MinimalECommunityDto?, meterDataRT: BufferedMeterDataRTDto?) {
    val formatter = ECommunityFormatter(LocalContext.current)

    var memberString = "" // concatenated members
    val lastMemberIndex = eCommunity?.members?.size?.minus(1) ?: 0 // index of last member
    eCommunity?.members?.forEachIndexed { index, member ->
        var splitter = ""
        if (index != lastMemberIndex) {
            // after last member there should be no splitter
            splitter = if (index == Constants.MAX_MEMBERS_TOP_BAR - 1) ", ..." else ", "
        }
        memberString = "$memberString${member.userName}$splitter"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.blue))
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            // eCommunity image
            Image(
                // painter = rememberAsyncImagePainter("https://avatars.githubusercontent.com/u/45870302?v=4"), // load image from URL
                painter = painterResource(id = R.drawable.ic_default_group_pic),
                contentDescription = stringResource(R.string.e_community_top_bar_group_icon_desc),
                contentScale = ContentScale.Crop, // crop the image if it's not a square
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape), // clip to the circle shape
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
            ) {
                // eCommunity name
                Text(
                    text = eCommunity?.name ?: "",
                    color = colorResource(id = R.color.white),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                // eCommunity members
                Text(
                    text = memberString,
                    color = colorResource(id = R.color.white),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )
            }
        }
        if (meterDataRT != null) {
            // show real time data if available
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    // feed in
                    Text(
                        text = formatter.formatSmartMeterValue(meterDataRT.eCommunityActivePowerMinus),
                        color = colorResource(id = R.color.value_good),
                        fontSize = 16.sp,
                    )
                    // consumption
                    Text(
                        text = formatter.formatSmartMeterValue(meterDataRT.eCommunityActivePowerPlus),
                        color = colorResource(id = R.color.value_bad),
                        fontSize = 16.sp,
                    )
                }
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    // feed in icon (sun)
                    Icon(
                        imageVector = Icons.Outlined.WbSunny,
                        contentDescription = stringResource(R.string.e_community_top_bar_feed_in_icon_desc),
                        tint = colorResource(id = R.color.value_good),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp)
                    )
                    // consumption icon (plug)
                    Icon(
                        imageVector = Icons.Outlined.Power,
                        contentDescription = stringResource(R.string.e_community_top_bar_consumption_icon_desc),
                        tint = colorResource(id = R.color.value_bad),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 4.dp)
                    )
                }
            }
        }
    }
}