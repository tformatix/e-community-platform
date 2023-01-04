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
import at.fhooe.ecommunity.util.Formatter

@Composable
fun ECommunityPerformance(performance: PerformanceDto?) {
    val formatter = Formatter(LocalContext.current)
    val selectItems = listOf(
        stringResource(R.string.e_community_performance_day),
        stringResource(R.string.e_community_performance_week),
        stringResource(R.string.e_community_performance_month),
        stringResource(R.string.e_community_performance_year),
        stringResource(R.string.e_community_performance_total)
    )

    var goodForecastsString = "-/-"
    var forecastDeviationString = "-"

    if(performance != null){
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
            Text(
                text = stringResource(R.string.e_community_performance_title),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.Center)
            )
            DropDown(
                items = selectItems,
                onSelected = {
                    // TODO
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
        Row {
            ECommunityTile(
                title = stringResource(R.string.e_community_performance_good_forecasts),
                content = goodForecastsString,
                color = colorResource(id = R.color.value_good),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
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