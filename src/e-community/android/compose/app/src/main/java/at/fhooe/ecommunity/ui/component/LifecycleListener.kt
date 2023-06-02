package at.fhooe.ecommunity.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * listen to event of the android lifecycle
 * @param _onEvent event of lifecycle (e.g.: ON_RESUME, ON_START, ..)
 * @see Composable
 */
@Composable
fun LifecycleListener(_onEvent: (event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(newValue = _onEvent)
    val lifecycleOwner = rememberUpdatedState(newValue = LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {

        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { source, event ->
            eventHandler.value(event)
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}