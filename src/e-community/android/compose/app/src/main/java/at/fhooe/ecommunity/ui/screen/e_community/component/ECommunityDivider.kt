package at.fhooe.ecommunity.ui.screen.e_community.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import at.fhooe.ecommunity.R

/**
 * horizontal line
 * @see Composable
 */
@Composable
fun ECommunityDivider() {
    Divider(
        color = colorResource(id = R.color.gray_light),
        thickness = 1.dp,
        modifier = Modifier
            .padding(vertical = 16.dp)
    )
}