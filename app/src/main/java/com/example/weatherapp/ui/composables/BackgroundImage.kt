package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.model.WeatherVisuals

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BackgroundImage(targetPreset: WeatherVisuals) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
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

