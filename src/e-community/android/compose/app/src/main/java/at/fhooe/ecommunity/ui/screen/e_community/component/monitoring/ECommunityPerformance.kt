package at.fhooe.ecommunity.ui.screen.e_community.component.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.PerformanceDto
import at.fhooe.ecommunity.ui.component.DropDown
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile
import at.fhooe.ecommunity.util.ECommunityFormatter

/**
 * displays the user's performance (number of good forecasts and forecast deviation)
 * @param performance smart meter performance
 * @param onDurationDaysChanged handler for a changed performance period
 * @see Composable
 */
@Composable
fun ECommunityPerformance(performance: PerformanceDto?, onDurationDaysChanged: (durationDays: Int) -> Unit) {
    val formatter = ECommunityFormatter(LocalContext.current)

    // performance periods
    val selectItems = mapOf(
        stringResource(R.string.e_community_performance_day) to 1,
        stringResource(R.string.e_community_performance_week) to 7,
        stringResource(R.string.e_community_performance_month) to 30,
        stringResource(R.string.e_community_performance_year) to 365,
        stringResource(R.string.e_community_performance_total) to -1
    )

    // default values
    var goodForecastsString = "-/-"
    var forecastDeviationString = "-"

    if(performance != null){
        // if performance is not null --> replace default values
        goodForecastsString = "${performance.goodForecastCount}/${performance.forecastCount}"
        forecastDeviationString = formatter.formatSmartMeterValue(performance.wrongForecasted ?: 0, true)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        ) {
            // headline of the two tiles
            Text(
                text = stringResource(R.string.e_community_performance_title),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Center)
            )
            // drop down for performance periods
            DropDown(
                items = selectItems.keys.toList(),
                onSelected = { _, item ->
                    selectItems[item]?.let { onDurationDaysChanged(it) }
                },
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
        Row {
            // tile for number of good forecasts
            ECommunityTile(
                title = stringResource(R.string.e_community_performance_good_forecasts),
                content = goodForecastsString,
                color = colorResource(id = R.color.value_good),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            // tile for forecast deviation
            ECommunityTile(
                title = stringResource(R.string.e_community_performance_forecast_deviation),
                content = forecastDeviationString,
                color = colorResource(id = R.color.value_bad),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }
}