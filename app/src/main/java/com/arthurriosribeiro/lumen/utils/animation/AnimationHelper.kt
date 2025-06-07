package com.arthurriosribeiro.lumen.utils.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun shimmerAnimation(colors: List<Color>, duration: Int = 1800) : Brush {
    val transition = rememberInfiniteTransition()
    val shimmerOffset = transition.animateFloat(
        initialValue = 0F,
        targetValue = 1000F,
        animationSpec = infiniteRepeatable(animation = tween(duration, easing = LinearEasing))
    )

    val shimmerBrush = Brush.linearGradient(
        colors = colors,
        start = Offset.Zero,
        end = Offset(x = shimmerOffset.value, y = shimmerOffset.value)
    )

    return shimmerBrush
}