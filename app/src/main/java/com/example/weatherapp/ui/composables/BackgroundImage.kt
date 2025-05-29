package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.model.WeatherVisuals

@Composable
fun BackgroundImage(weatherVisuals: WeatherVisuals) {
    Log.d("BackgroundImage", "weatherVisuals: $weatherVisuals")
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(weatherVisuals.backgroundResId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = weatherVisuals.colorFilter,
            modifier = Modifier.fillMaxSize()
        )
        weatherVisuals.lottieAnimationResId?.let { animResId ->
            LottieAnimationPlayer(animResId)
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

