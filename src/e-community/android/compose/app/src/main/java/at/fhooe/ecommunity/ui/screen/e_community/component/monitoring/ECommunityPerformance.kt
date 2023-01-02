package at.fhooe.ecommunity.ui.screen.e_community.component.monitoring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.ui.component.DropDown
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile

@Composable
fun ECommunityPerformance() {
    val selectItems = listOf(
        stringResource(R.string.e_community_performance_day),
        stringResource(R.string.e_community_performance_week),
        stringResource(R.string.e_community_performance_month),
        stringResource(R.string.e_community_performance_year),
        stringResource(R.string.e_community_performance_total)
    )

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
                content = "3",
                color = colorResource(id = R.color.value_good),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            ECommunityTile(
                title = stringResource(R.string.e_community_performance_forecast_deviation),
                content = "0.5 kWh",
                color = colorResource(id = R.color.value_bad),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }
}