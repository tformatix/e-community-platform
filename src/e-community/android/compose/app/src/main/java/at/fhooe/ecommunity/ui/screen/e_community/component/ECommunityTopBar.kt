package at.fhooe.ecommunity.ui.screen.e_community.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R

@Composable
fun ECommunityTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.blue))
            .padding(16.dp, 8.dp, 12.dp, 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Image(
                // painter = rememberAsyncImagePainter("https://avatars.githubusercontent.com/u/45870302?v=4"), // load image from URL
                painter = painterResource(id = R.drawable.ic_default_group_pic),
                contentDescription = "Group Icon",
                contentScale = ContentScale.Crop, // crop the image if it's not a square
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape), // clip to the circle shape
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp),
            ) {
                Text(
                    text = "Amazing Community",
                    color = colorResource(id = R.color.white),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Fischer, Hofer, Puchner, Zauner, ...",
                    color = colorResource(id = R.color.white),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_sun),
                        contentDescription = "Feed in",
                        tint = colorResource(id = R.color.value_good),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = "10.1 kW",
                        color = colorResource(id = R.color.value_good),
                        fontSize = 16.sp,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_power),
                        contentDescription = "Consumption",
                        tint = colorResource(id = R.color.value_bad),
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = "15.3 kW",
                        color = colorResource(id = R.color.value_bad),
                        fontSize = 16.sp,
                    )
                }
            }
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_forward),
                contentDescription = "Detail eCommunity Screen",
                tint = colorResource(id = R.color.white),
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
    }
}