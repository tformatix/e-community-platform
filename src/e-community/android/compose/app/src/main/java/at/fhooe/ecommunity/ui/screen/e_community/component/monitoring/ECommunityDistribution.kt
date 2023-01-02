package at.fhooe.ecommunity.ui.screen.e_community.component.monitoring

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.CurrentPortionDto
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile
import at.fhooe.ecommunity.util.Formatter

@Composable
fun ECommunityDistribution(currentPortion: CurrentPortionDto) {
    val formatter = Formatter(LocalContext.current)
    val flexibility = currentPortion.flexibility ?: 0
    val flexibilitySign =
        if (flexibility > 0) "+"
        else if (flexibility < 0) "-"
        else ""

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
            content = formatter.formatSmartMeterValue(currentPortion.estimatedActiveEnergyPlus ?: 0, true),
            subContent = "(${flexibilitySign}${formatter.formatSmartMeterValue(flexibility, true)})",
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )
        ECommunityTile(
            title = stringResource(R.string.e_community_distribution_assigned),
            content = formatter.formatSmartMeterValue(
                (currentPortion.estimatedActiveEnergyPlus ?: 0) + (currentPortion.deviation ?: 0),
                true
            ),
            color = colorResource(id = R.color.value_bad),
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )
    }
}