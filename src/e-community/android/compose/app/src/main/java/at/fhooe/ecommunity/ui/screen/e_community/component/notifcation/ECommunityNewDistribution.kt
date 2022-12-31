package at.fhooe.ecommunity.ui.screen.e_community.component.notifcation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
            .padding(8.dp)
    ) {
        // header
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // remaining time
            Text(
                text = "1:20",
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
            // title
            Text(
                text = "New Hour - New Energy Distribution!",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center)
            )
            // accept icon
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_check),
                contentDescription = "Accept",
                tint = colorResource(id = R.color.value_good),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }
        // more energy available
        Text(
            text = "Probably 4 kWh energy still available - Increase Flexibility?",
            color = colorResource(id = R.color.value_good),
            fontSize = 10.sp
        )
        // forecasts are missing
        Text(
            text = "Missing Forecasts from 2 Smart Meters - Calculation may be incorrect!",
            color = colorResource(id = R.color.value_bad),
            fontSize = 10.sp
        )
        // input flexibility
        TextField(
            value = flexibility,
            onValueChange = { flexibility = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                errorLabelColor = colorResource(id = R.color.value_bad),
                errorCursorColor = colorResource(id = R.color.value_bad),
                errorIndicatorColor = colorResource(id = R.color.value_bad),
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
            label = {
                Text(
                    text = "Change Flexibility (kWh):",
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            },
            isError = isFlexibilityError,
            trailingIcon = {
                if (isFlexibilityError) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "error",
                        tint = colorResource(id = R.color.value_bad)
                    )
                }
            },
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(0.dp)
        )
        // tiles
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ECommunityTile(
                title = "estimated consumption",
                content = "1.7 kWh",
                background = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
            ECommunityTile(
                title = "assigned",
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