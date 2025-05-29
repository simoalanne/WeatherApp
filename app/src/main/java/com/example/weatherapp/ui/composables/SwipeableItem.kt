package com.example.weatherapp.ui.composables

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import kotlinx.coroutines.delay

enum class Offset {
    LEFT,
    RIGHT,
    CENTER
}


@Composable
fun SwipeableItem(
    contentKey: Any,
    content: @Composable () -> Unit,
    start: Offset = Offset.LEFT,
    end: Offset = Offset.CENTER,
    initialDelayMs: Int = 0,
    onAnimationEnd: () -> Unit = {},
    shouldPlayAnimation: Boolean = true,
) {
    var isInitialLoad by remember { mutableStateOf(true) }
    val containerWidth = LocalWindowInfo.current.containerSize.width.toFloat()
    val startOffset = when (start) {
        Offset.LEFT -> -containerWidth
        Offset.RIGHT -> containerWidth
        else -> 0f
    }
    val endOffset = when (end) {
        Offset.LEFT -> -containerWidth
        Offset.RIGHT -> containerWidth
        else -> 0f
    }

    val offsetX = remember { Animatable(startOffset) }

    LaunchedEffect(contentKey) {
        if (!shouldPlayAnimation) {
            isInitialLoad = false
            onAnimationEnd()
            return@LaunchedEffect
        }
        if (isInitialLoad) {
            onAnimationEnd()
            isInitialLoad = false
            delay(initialDelayMs.toLong())
            offsetX.animateTo(
                targetValue = endOffset,
                animationSpec = tween(durationMillis = 500)
            )
        } else {
            offsetX.animateTo(
                targetValue = startOffset,
                animationSpec = tween(durationMillis = 500)
            )

            offsetX.snapTo(startOffset)
            onAnimationEnd()

            offsetX.animateTo(
                targetValue = endOffset,
                animationSpec = tween(durationMillis = 500)
            )
        }
    }

    Box(
        modifier = Modifier.graphicsLayer {
            translationX = offsetX.value
        }
    ) {
        if (offsetX.value != startOffset) {
            content()
        }
    }
}
