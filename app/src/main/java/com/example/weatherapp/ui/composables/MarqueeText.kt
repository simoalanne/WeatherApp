package com.example.weatherapp.ui.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned

/**
 * Renders a text that scrolls horizontally back and forth. The build in basicMarquee function
 * does not support this kind of scrolling so that's why this is implemented. AI helped to write
 * the animation code for this composable because i had no idea how to write animations with
 * jetpack compose.
 *
 * @param text The text to be rendered.
 * @param modifier The modifier to be applied to the composable. Should include fixed width or a
 * weight modifier in order for the dimensions to be able to be calculated.
 *
 */
@Composable
fun MarqueeText(text: String, modifier: Modifier = Modifier) {
    var containerWidth by remember { mutableIntStateOf(0) }
    var textWidth by remember { mutableIntStateOf(0) }

    // calculate the offset and if negative (text smaller than container) set it to 0 so it wont
    // try to unnecessarily animate
    val offset = (textWidth - containerWidth).coerceAtLeast(0)

    val transition = rememberInfiniteTransition() // creates a transition that runs forever
    val animatedOffset by transition.animateFloat( // animate float value
        initialValue = 0f,
        // target is to move the text till the end of the text is visible
        targetValue = -offset.toFloat(),
        // animation should repeat forever
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (offset * 15).coerceAtLeast(1000),
                easing = LinearEasing // constant speed
            ),
            repeatMode = RepeatMode.Reverse // TODO: looks bad should rather start over and pause on start and end
        ),
    )

    Row(
        modifier = modifier
            .clipToBounds() // Hides the overflow
            .onGloballyPositioned { // get the width of the container
                containerWidth = it.size.width
            }
    ) {
        Text(
            text,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                // allow text to overflow past the end of the row.
                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                // graphicsLayer uses the devices gpu to perform the animation so it's more efficient
                // compared to adjusting the offset via the modifier.
                .graphicsLayer {
                    translationX = if (offset > 0) animatedOffset else 0f
                }
                .onGloballyPositioned { // get the width of the text
                    textWidth = it.size.width
                }
        )
    }
}
