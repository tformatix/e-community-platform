package at.fhooe.ecommunity.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * loading indicator which uses the whole width
 * @param modifier modifier elements
 * @see Composable
 */
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        modifier = modifier.fillMaxWidth()
    )
}