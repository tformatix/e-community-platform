package at.fhooe.ecommunity.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.TextUnit
import at.fhooe.ecommunity.R

/**
 * drop down component
 * @param items list of selectable options
 * @param onSelected lambda called when selected
 * @param fontSize font size of the text
 * @param modifier modifier elements
 * @param preSelected pre selected index
 * @see Composable
 */
@Composable
fun DropDown(
    items: List<String>,
    onSelected: (idx: Int, item: String) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    preSelected: Int = 0) {
    var expanded by remember { mutableStateOf(false) } // is menu expanded
    var selectedIndex by remember { mutableStateOf(preSelected) } // index of the selected item

    if(items.isNotEmpty()) {
        // items are available
        Box(
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = { expanded = true })
            ) {
                Text(
                    text = items[selectedIndex],
                    fontSize = fontSize,
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "drop down"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // close menu
            ) {
                items.forEachIndexed { idx, item ->
                    val isSelected = idx == selectedIndex
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex = idx
                            expanded = false
                            onSelected(idx, item)
                        },
                        modifier = Modifier
                            .background(if (isSelected) colorResource(id = R.color.gray_light) else Color.Transparent)
                    ) {
                        if (isSelected) {
                            Text(
                                text = item,
                                color = Color.Black
                            )
                        } else {
                            Text(text = item)
                        }
                    }
                }
            }
        }
    }
}