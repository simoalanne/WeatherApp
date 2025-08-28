package com.simoalanne.weatherapp.ui.composables

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.simoalanne.weatherapp.model.WeatherVisuals

/**
 * Composable for the dynamic background image to be shown in either weather screen or weather preview
 * screen.
 *
 * @param targetPreset The target preset to be used for the background image, it's color filter and lottie animation.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BackgroundImage(targetPreset: WeatherVisuals) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Do cross fade animation when the background image changes.
        Crossfade(targetState = targetPreset.backgroundResId) { target ->
            Image(
                painter = painterResource(target),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                colorFilter = targetPreset.colorFilter,
                modifier = Modifier.fillMaxSize()
            )
            targetPreset.lottieAnimationResId?.let { animResId ->
                LottieAnimationPlayer(animResId)
            }
        }
    }
}

@Composable
fun LottieAnimationPlayer(id: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(id))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(align = Alignment.Top)
    )
}

