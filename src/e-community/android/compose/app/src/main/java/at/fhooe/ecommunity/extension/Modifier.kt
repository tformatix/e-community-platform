package at.fhooe.ecommunity.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput

/**
 * extension method for the Modifier class which enables/disables gestures on a Composable
 * @param _disabled are user gestures possible?
 */
fun Modifier.gesturesDisabled(_disabled: Boolean = true) =
    if (_disabled) {
        // user gestures shouldn't be possible
        pointerInput(Unit) {
            awaitPointerEventScope {
                // wait for all new pointer events
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consume)
                }
            }
        }
    } else {
        this
    }
