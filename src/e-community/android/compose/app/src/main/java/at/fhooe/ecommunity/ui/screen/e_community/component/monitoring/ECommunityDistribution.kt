package at.fhooe.ecommunity.ui.screen.e_community.component.monitoring

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import at.fhooe.ecommunity.util.ECommunityFormatter
import kotlin.math.abs

/**
 * displays hourly distribution (forecast and assigned)
 * @param currentPortion current portion of smart meter
 * @see Composable
 */
@Composable
fun ECommunityDistribution(currentPortion: CurrentPortionDto?) {
    val formatter = ECommunityFormatter(LocalContext.current)

    // default values
    var forecastString = "-"
    var flexibilityString = ""
    var assignedString = "-"
    var assignedColor = Color.Black

    if (currentPortion != null) {
        // if current portion is not null --> replace default values
        val consumption = currentPortion.estimatedActiveEnergyPlus ?: 0
        forecastString = formatter.formatSmartMeterValue(consumption, true)

        val flexibility = currentPortion.flexibility ?: 0
        flexibilityString =
            if (flexibility > 0) "(+${formatter.formatSmartMeterValue(flexibility, true)})"
            else if (flexibility < 0) "(-${formatter.formatSmartMeterValue(flexibility, true)})"
            else ""

        val assigned = consumption + (currentPortion.deviation ?: 0)
        assignedString = formatter.formatSmartMeterValue(
            assigned,
            true
        )

        val consumptionWithFlexibility = consumption + flexibility
        assignedColor =
            if (assigned >= consumptionWithFlexibility) colorResource(id = R.color.value_good) // sufficient
            else colorResource(id = R.color.value_bad) // not sufficient
    }

    // headline of the two tiles
    Text(
        text = stringResource(R.string.e_community_distribution_title),
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
    )
    if (currentPortion?.unassignedActiveEnergyMinus != null && currentPortion.unassignedActiveEnergyMinus != 0) {
        // more/less energy available
        val textId: Int
        val color: Color
        if (currentPortion.unassignedActiveEnergyMinus > 0) {
            textId = R.string.e_community_distribution_more_energy
            color = colorResource(id = R.color.value_good)
        } else {
            textId = R.string.e_community_distribution_less_energy
            color = colorResource(id = R.color.value_bad)
        }
        Text(
            text = stringResource(
                textId,
                formatter.formatSmartMeterValue(abs(currentPortion.unassignedActiveEnergyMinus), true)
            ),
            color = color,
            fontSize = 10.sp
        )
    }
    if (currentPortion?.missingSmartMeterCount != null && currentPortion.missingSmartMeterCount > 0) {
        // forecasts are missing
        Text(
            text = stringResource(R.string.e_community_distribution_forecasts_missing, currentPortion.missingSmartMeterCount),
            color = colorResource(id = R.color.value_bad),
            fontSize = 10.sp
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
    ) {
        // forecast tile
        ECommunityTile(
            title = stringResource(R.string.e_community_distribution_forecast),
            content = forecastString,
            subContent = flexibilityString,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )
        // assigned tile
        ECommunityTile(
            title = stringResource(R.string.e_community_distribution_assigned),
            content = assignedString,
            color = assignedColor,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        )
    }
}