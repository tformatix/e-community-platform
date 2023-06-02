package at.fhooe.ecommunity.ui.screen.e_community.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R

/**
 * basic tile
 * @param title headline
 * @param content body
 * @param modifier (optional) applied modifier to tile
 * @param background (optional) background color (default: light gray)
 * @param color (optional) foreground color (default: black)
 * @param subContent (optional) content next to content (little bit smaller)
 * @param icon (optional) icon in front of content
 * @see Composable
 */
@Composable
fun ECommunityTile(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    background: Color = colorResource(id = R.color.gray_light),
    color: Color = Color.Black,
    subContent: String? = null,
    icon: ImageVector? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)) // round shape
            .background(background)
            .padding(8.dp)
    ) {
        // title
        Text(
            text = title,
            color = color,
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
        )
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            if (icon != null) {
                // show icon if available
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.e_community_tile_icon_desc),
                    tint = color,
                    modifier = Modifier
                        .size(23.dp)
                        .padding(bottom = 2.dp, end = 2.dp)
                )
            }
            // content
            Text(
                text = content,
                color = color,
                fontSize = 20.sp,
            )
            if (subContent != null) {
                // show sub-content if available
                Text(
                    text = subContent,
                    color = color,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(start = 1.dp, bottom = 3.dp)
                )
            }
        }
    }
}