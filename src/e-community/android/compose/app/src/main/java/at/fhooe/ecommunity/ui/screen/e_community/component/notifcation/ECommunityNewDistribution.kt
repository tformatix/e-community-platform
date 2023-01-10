package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.NewDistributionDto
import at.fhooe.ecommunity.data.remote.openapi.cloud.models.NewPortionDto
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile
import at.fhooe.ecommunity.util.Formatter
import kotlin.math.abs

@Composable
fun ECommunityNewDistribution(
    newDistribution: NewDistributionDto,
    onAccepted: (portion: NewPortionDto, flexibility: Int) -> Unit
) {
    val formatter = Formatter(LocalContext.current)

    newDistribution.newPortions?.forEach { portion ->
        var flexibility by remember { mutableStateOf((portion.flexibility ?: 0).toString()) }
        val flexibilityInt = flexibility.toIntOrNull()
        val isFlexibilityError = flexibilityInt == null

        val assignedColor =
            if (abs(portion.deviation ?: 0) <= abs(portion.flexibility ?: 0)) colorResource(id = R.color.value_good)
            else colorResource(id = R.color.value_bad)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.activity_vertical_margin))
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colorResource(id = R.color.gray_light))
                .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
        ) {
            // header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = stringResource(R.string.e_community_new_distribution_title),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = portion.smartMeterName ?: "",
                        color = Color.Black,
                        fontSize = 10.sp,
                    )
                }
                // accept icon
                IconButton(
                    enabled = !isFlexibilityError,
                    onClick = {
                        onAccepted(portion, flexibilityInt!!)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = stringResource(R.string.e_community_new_distribution_accept_icon_desc),
                        tint = colorResource(id = R.color.value_good),
                    )
                }
            }
            if (newDistribution.unassignedActiveEnergyMinus != null && abs(newDistribution.unassignedActiveEnergyMinus) > 0) {
                // more/less energy available
                val textId : Int
                val color: Color
                if (newDistribution.unassignedActiveEnergyMinus > 0){
                    textId = R.string.e_community_new_distribution_more_energy
                    color = colorResource(id = R.color.value_good)
                }else{
                    textId = R.string.e_community_new_distribution_less_energy
                    color = colorResource(id = R.color.value_bad)
                }
                Text(
                    text = stringResource(
                        textId,
                        formatter.formatSmartMeterValue(newDistribution.unassignedActiveEnergyMinus, true)
                    ),
                    color = color,
                    fontSize = 10.sp
                )
            }
            if (newDistribution.missingSmartMeterCount != null && newDistribution.missingSmartMeterCount > 0) {
                // forecasts are missing
                Text(
                    text = stringResource(R.string.e_community_new_distribution_forecasts_missing, newDistribution.missingSmartMeterCount),
                    color = colorResource(id = R.color.value_bad),
                    fontSize = 10.sp
                )
            }
            // input flexibility
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.e_community_new_distribution_change_flexibility),
                    fontStyle = FontStyle.Italic,
                    color = if (isFlexibilityError) colorResource(id = R.color.value_bad) else Color.Black,
                    fontSize = 12.sp,
                )
                if (isFlexibilityError) {
                    Text(
                        text = stringResource(R.string.e_community_new_distribution_change_flexibility_error),
                        color = colorResource(id = R.color.value_bad),
                        style = MaterialTheme.typography.caption,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            flexibility = (flexibilityInt!! - 100).toString()
                        },
                        enabled = !isFlexibilityError
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RemoveCircleOutline,
                            tint = Color.Black,
                            contentDescription = stringResource(R.string.e_community_new_distribution_change_flexibility_minus_icon_desc)
                        )
                    }
                    BasicTextField(
                        value = flexibility,
                        onValueChange = { flexibility = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = if (isFlexibilityError) colorResource(id = R.color.value_bad) else Color.Black
                        ),
                    )
                    IconButton(
                        onClick = {
                            flexibility = (flexibilityInt!! + 100).toString()
                        },
                        enabled = !isFlexibilityError
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AddCircleOutline,
                            tint = Color.Black,
                            contentDescription = stringResource(R.string.e_community_new_distribution_change_flexibility_plus_icon_desc)
                        )
                    }
                }
            }
            // tiles
            val consumption = portion.estimatedActiveEnergyPlus ?: 0
            val assigned = (portion.estimatedActiveEnergyPlus ?: 0) + (portion.deviation ?: 0)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ECommunityTile(
                    title = stringResource(R.string.e_community_new_distribution_estimated_consumption),
                    content = formatter.formatSmartMeterValue(consumption, true),
                    background = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                ECommunityTile(
                    title = stringResource(R.string.e_community_new_distribution_assigned),
                    content = formatter.formatSmartMeterValue(
                        assigned,
                        true
                    ),
                    color = assignedColor,
                    background = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }
        }
        ECommunityDivider()
    }
}