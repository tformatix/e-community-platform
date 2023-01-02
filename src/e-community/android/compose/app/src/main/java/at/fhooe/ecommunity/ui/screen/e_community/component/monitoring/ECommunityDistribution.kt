package at.fhooe.ecommunity.ui.screen.e_community.component.monitoring

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile

@Composable
fun ECommunityDistribution() {
    Text(
        text = stringResource(R.string.e_community_distribution_title),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier
            .padding(bottom = 4.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
    ) {
        ECommunityTile(
            title = stringResource(R.string.e_community_distribution_forecast),
            content = "2.3 kWh",
            subContent = "(+0.3 kWh)",
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )
        ECommunityTile(
            title = stringResource(R.string.e_community_distribution_assigned),
            content = "2.1 kWh",
            color = colorResource(id = R.color.value_bad),
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )
    }
}