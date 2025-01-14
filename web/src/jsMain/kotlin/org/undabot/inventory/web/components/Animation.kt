package org.product.inventory.web.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AnimationIterationCount
import com.varabyte.kobweb.compose.css.CSSAnimation
import com.varabyte.kobweb.compose.css.CSSTimeNumericValue
import com.varabyte.kobweb.silk.components.animation.Keyframes
import com.varabyte.kobweb.silk.components.animation.toAnimation
import org.jetbrains.compose.web.css.AnimationFillMode
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.ms

@Composable
fun Keyframes.toPersistentAnimation(
    duration: CSSTimeNumericValue = 250.ms,
    timingFunction: AnimationTimingFunction? = AnimationTimingFunction.EaseIn,
    iterationCount: AnimationIterationCount? = AnimationIterationCount.of(1),
    fillMode: AnimationFillMode? = AnimationFillMode.Forwards,
): CSSAnimation = toAnimation(
    duration = duration,
    timingFunction = timingFunction,
    iterationCount = iterationCount,
    fillMode = fillMode,
)
