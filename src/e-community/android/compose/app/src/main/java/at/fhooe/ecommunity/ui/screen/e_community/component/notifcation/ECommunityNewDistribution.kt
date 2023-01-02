package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.fhooe.ecommunity.R
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityDivider
import at.fhooe.ecommunity.ui.screen.e_community.component.ECommunityTile

@Preview
@Composable
fun ECommunityNewDistribution() {
    var flexibility by remember { mutableStateOf("-0.3") }
    val isFlexibilityError = flexibility.toDoubleOrNull() == null

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
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Fischer's House 1",
                    fontSize = 10.sp,
                )
            }
            // accept icon
            IconButton(
                onClick = {
                    // TODO
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
        // more energy available
        Text(
            text = stringResource(R.string.e_community_new_distribution_more_energy, 4, "kWh"),
            color = colorResource(id = R.color.value_good),
            fontSize = 10.sp
        )
        // forecasts are missing
        Text(
            text = stringResource(R.string.e_community_new_distribution_forecasts_missing, 2),
            color = colorResource(id = R.color.value_bad),
            fontSize = 10.sp
        )
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
                        flexibility = ((flexibility.toDouble() * 10 - 1) / 10).toString()
                    },
                    enabled = !isFlexibilityError
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RemoveCircleOutline,
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
                    )
                )
                IconButton(
                    onClick = {
                        flexibility = ((flexibility.toDouble() * 10 + 1) / 10).toString()
                    },
                    enabled = !isFlexibilityError
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AddCircleOutline,
                        contentDescription = stringResource(R.string.e_community_new_distribution_change_flexibility_plus_icon_desc)
                    )
                }
            }
        }
        // tiles
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ECommunityTile(
                title = stringResource(R.string.e_community_new_distribution_estimated_consumption),
                content = "1.7 kWh",
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            ECommunityTile(
                title = stringResource(R.string.e_community_new_distribution_assigned),
                content = "1.6 kWh",
                color = colorResource(id = R.color.value_good),
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )
        }
    }

    ECommunityDivider()
}