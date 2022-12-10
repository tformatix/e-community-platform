package at.fhooe.ecommunity.ui.screen.e_community

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import at.fhooe.ecommunity.extension.gesturesDisabled
import at.fhooe.ecommunity.model.LoadingState
import at.fhooe.ecommunity.ui.component.LoadingIndicator
import coil.compose.rememberAsyncImagePainter

@Composable
fun ECommunityScreen(_viewModel: ECommunityViewModel, _navController: NavHostController) {
    val viewModelState by _viewModel.mState.collectAsState()
    var isLoading = false // indicator whether the view model state is RUNNING (loading) and the gestures of the underlying layout should be disabled

    when (viewModelState.mState) {
        LoadingState.State.RUNNING -> {
            // view model operation is loading
            LoadingIndicator() // show loading indicator
            isLoading = true
        }
        LoadingState.State.SUCCESS -> {
            // view model operation succeeded
            _viewModel.backToIdle() // bring the view model back to the idle state
        }
        LoadingState.State.FAILED -> {
            // view model operation failed
            _viewModel.backToIdle() // bring the view model back to the idle state
        }
        else -> {}
    }

    Box(
        modifier = Modifier
            .gesturesDisabled(isLoading)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            Image(
                painter = rememberAsyncImagePainter("https://avatars.githubusercontent.com/u/45870302?v=4"),
                contentDescription = "avatar",
                contentScale = ContentScale.Crop, // crop the image if it's not a square
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape) // clip to the circle shape
            )
        }
    }
}