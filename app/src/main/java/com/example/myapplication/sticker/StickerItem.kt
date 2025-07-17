package com.example.myapplication.sticker

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateRotation
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun StickerItem(
    sticker: StickerModel,
    isSelected: Boolean,
    onUpdate: (StickerModel) -> Unit,
    onSelected: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var position by remember { mutableStateOf(sticker.position) }
    var scale by remember { mutableStateOf(sticker.scale) }
    var rotation by remember { mutableStateOf(sticker.rotation) }
    var isFlipped by remember { mutableStateOf(sticker.isFlipped) }

    val density = LocalDensity.current
    var contentSize by remember { mutableStateOf(IntSize(250, 250)) }

    var lastTouchPoint by remember { mutableStateOf(Offset.Zero) }
    var lastMidPoint by remember { mutableStateOf(Offset.Zero) }
    var lastRotation by remember { mutableStateOf(rotation) }
    var lastScale by remember { mutableStateOf(scale) }
    var initialAngle by remember { mutableStateOf(0f) }
    var initialDistance by remember { mutableStateOf(1f) }
    var downTime by remember { mutableStateOf(0L) }
    var downPosition by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(position, scale, rotation, isFlipped) {
        onUpdate(sticker.copy(position = position, scale = scale, rotation = rotation, isFlipped = isFlipped))
    }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                .graphicsLayer {
                    scaleX = if (isFlipped) -scale else scale
                    scaleY = scale
                    rotationZ = rotation
                    transformOrigin = TransformOrigin.Center
                }
                .pointerInteropFilter { event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            downTime = event.eventTime
                            downPosition = Offset(event.rawX, event.rawY)
                            lastTouchPoint = downPosition
                            lastScale = scale
                            lastRotation = rotation
                        }

                        MotionEvent.ACTION_POINTER_DOWN -> {
                            if (event.pointerCount >= 2) {
                                val dx = event.getX(1) - event.getX(0)
                                val dy = event.getY(1) - event.getY(0)

                                initialDistance = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
                                initialAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

                                val midX = (event.getX(0) + event.getX(1)) / 2f
                                val midY = (event.getY(0) + event.getY(1)) / 2f
                                lastMidPoint = Offset(midX, midY)

                                lastScale = scale
                                lastRotation = rotation
                            }
                        }

                        MotionEvent.ACTION_MOVE -> {
                            if (event.pointerCount == 1) {
                                val current = Offset(event.rawX, event.rawY)
                                val delta = current - lastTouchPoint
                                position += delta
                                lastTouchPoint = current
                            }

                            if (event.pointerCount >= 2) {
                                val x0 = event.getX(0)
                                val y0 = event.getY(0)
                                val x1 = event.getX(1)
                                val y1 = event.getY(1)

                                val dx = x1 - x0
                                val dy = y1 - y0
                                val currentDistance = sqrt(dx * dx + dy * dy).coerceAtLeast(1f)
                                val currentAngle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

                                // Calculate relative change from initial
                                val scaleFactor = currentDistance / initialDistance
                                scale = (lastScale * scaleFactor).coerceIn(0.3f, 5f)

                                val angleDelta = currentAngle - initialAngle
                                rotation = lastRotation + angleDelta

                                // Move midpoint only based on relative position
                                val midX = (x0 + x1) / 2f
                                val midY = (y0 + y1) / 2f
                                val currentMid = Offset(midX, midY)

                                val moveDelta = currentMid - lastMidPoint
                                position += moveDelta

                                // Don't reset anchors here
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            val upPosition = Offset(event.rawX, event.rawY)
                            val distance = (upPosition - downPosition).getDistance()
                            val duration = event.eventTime - downTime
                            if (distance < 20f && duration < 200) {
                                onSelected(sticker.id)
                            }
                        }

                        MotionEvent.ACTION_POINTER_UP,
                        MotionEvent.ACTION_CANCEL -> {
                            // Reset anchors ONLY when fingers are removed
                            lastScale = scale
                            lastRotation = rotation
                            initialDistance = 1f
                            initialAngle = 0f
                        }
                    }
                    true
                }





        ) {
            val borderModifier = if (isSelected) Modifier.border(1.dp, Color.Black.copy(alpha = 0.2f)) else Modifier

            when (val content = sticker.content) {
                is StickerContent.BitmapSticker -> {
                    Image(
                        bitmap = content.bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .onGloballyPositioned { contentSize = it.size }
                            .then(borderModifier)
                            .size(150.dp)
                    )
                }

                is StickerContent.TextSticker -> {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { contentSize = it.size }
                            .then(borderModifier)
                    ) {
                        androidx.compose.material3.Text(text = content.text, color = Color.Black)
                    }
                }

                is StickerContent.EmojiSticker -> {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { contentSize = it.size }
                            .then(borderModifier)
                    ) {
                        androidx.compose.material3.Text(text = content.emoji, fontSize = 64.sp)
                    }
                }
            }
        }

        if (isSelected) {
            val center = Offset(
                position.x + contentSize.width / 2f,
                position.y + contentSize.height / 2f
            )

            fun transformedCorner(offsetX: Float, offsetY: Float): Offset {
                val scaledX = offsetX * scale
                val scaledY = offsetY * scale
                val rad = Math.toRadians(rotation.toDouble()).toFloat()
                val cos = cos(rad)
                val sin = sin(rad)
                val rotatedX = scaledX * cos - scaledY * sin
                val rotatedY = scaledX * sin + scaledY * cos
                return center + Offset(rotatedX, rotatedY)
            }

            val topLeft = transformedCorner(-contentSize.width / 2f, -contentSize.height / 2f)
            val bottomLeft = transformedCorner(-contentSize.width / 2f, contentSize.height / 2f)
            val bottomRight = transformedCorner(contentSize.width / 2f, contentSize.height / 2f)

            val iconSizeDp = 24.dp
            val iconSizePx = with(density) { iconSizeDp.toPx() }
            val halfIconSizePx = iconSizePx / 2f

            @Composable
            fun iconBox(offset: Offset, onClick: () -> Unit, iconId: Int) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = offset.x - halfIconSizePx
                            translationY = offset.y - halfIconSizePx
                        }
                        .size(iconSizeDp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            iconBox(topLeft, { onDelete(sticker.id) }, R.drawable.sticker_ic_close_white_18dp)
            iconBox(bottomLeft, { isFlipped = !isFlipped }, R.drawable.sticker_ic_flip_white_18dp)

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = bottomRight.x - halfIconSizePx
                        translationY = bottomRight.y - halfIconSizePx
                    }
                    .size(iconSizeDp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            val dragDistance = dragAmount.getDistance()
                            val scaleDelta = dragDistance / 200f
                            scale = (scale + scaleDelta).coerceIn(0.3f, 5f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sticker_ic_scale_white_18dp),
                    contentDescription = "Resize",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}







/*
@Composable
fun StickerItem(
    sticker: StickerModel,
    isSelected: Boolean,
    onUpdate: (StickerModel) -> Unit,
    onSelected: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var position by remember { mutableStateOf(sticker.position) }
    var scale by remember { mutableStateOf(sticker.scale) }
    var rotation by remember { mutableStateOf(sticker.rotation) }
    var isFlipped by remember { mutableStateOf(sticker.isFlipped) }

    val density = LocalDensity.current
    var contentSize by remember { mutableStateOf(IntSize(250, 250)) }

    val debouncedUpdate = remember { mutableStateOf<StickerModel?>(null) }
    LaunchedEffect(position, scale, rotation, isFlipped) {
        debouncedUpdate.value = sticker.copy(
            position = position,
            scale = scale,
            rotation = rotation,
            isFlipped = isFlipped
        )
    }
    LaunchedEffect(debouncedUpdate.value) {
        debouncedUpdate.value?.let { snapshot ->
            delay(50)
            onUpdate(snapshot)
        }
    }

    val center = remember(position, contentSize) {
        Offset(
            position.x + contentSize.width / 2f,
            position.y + contentSize.height / 2f
        )
    }

    // Transformed corner calculations
    fun transformedCorner(offsetX: Float, offsetY: Float): Offset {
        val scaledX = offsetX * scale
        val scaledY = offsetY * scale
        val radians = Math.toRadians(rotation.toDouble()).toFloat()
        val cos = cos(radians)
        val sin = sin(radians)
        val rotatedX = scaledX * cos - scaledY * sin
        val rotatedY = scaledX * sin + scaledY * cos
        return center + Offset(rotatedX, rotatedY)
    }

    val topLeft = transformedCorner(-contentSize.width / 2f, -contentSize.height / 2f)
    val bottomLeft = transformedCorner(-contentSize.width / 2f, contentSize.height / 2f)
    val bottomRight = transformedCorner(contentSize.width / 2f, contentSize.height / 2f)

    // Gesture state
    var dragStartTouch by remember { mutableStateOf(Offset.Zero) }
    var dragStartPosition by remember { mutableStateOf(position) }
    var lastScale by remember { mutableStateOf(scale) }
    var lastRotation by remember { mutableStateOf(rotation) }
    var initialDistance = 0f
    var initialAngle = 0f
    var downTime by remember { mutableStateOf(0L) }
    var downPosition by remember { mutableStateOf(Offset.Zero) }

    Box(Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .offset { IntOffset(position.x.roundToInt(), position.y.roundToInt()) }
                .graphicsLayer {
                    scaleX = if (isFlipped) -scale else scale
                    scaleY = scale
                    rotationZ = rotation
                    transformOrigin = TransformOrigin.Center
                }
                .pointerInteropFilter { event ->
                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            downTime = event.eventTime
                            downPosition = Offset(event.rawX, event.rawY)
                            dragStartTouch = downPosition
                            dragStartPosition = position
                            lastScale = scale
                            lastRotation = rotation
                        }

                        MotionEvent.ACTION_POINTER_DOWN -> {
                            if (event.pointerCount == 2) {
                                val dx = event.getX(1) - event.getX(0)
                                val dy = event.getY(1) - event.getY(0)
                                initialDistance = sqrt(dx * dx + dy * dy)
                                initialAngle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                            }
                        }

                        MotionEvent.ACTION_MOVE -> {
                            if (event.pointerCount == 1) {
                                val currentTouch = Offset(event.rawX, event.rawY)
                                val delta = currentTouch - dragStartTouch
                                position = dragStartPosition + delta
                            } else if (event.pointerCount >= 2) {
                                val dx = event.getX(1) - event.getX(0)
                                val dy = event.getY(1) - event.getY(0)
                                val newDistance = sqrt(dx * dx + dy * dy)
                                val newAngle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()

                                val scaleFactor = (newDistance / initialDistance).coerceIn(0.3f, 5f)
                                scale = lastScale * scaleFactor
                                rotation = lastRotation + (newAngle - initialAngle)

                                val midX = (event.getX(0) + event.getX(1)) / 2f
                                val midY = (event.getY(0) + event.getY(1)) / 2f
                                val newMid = Offset(midX, midY)
                                val delta = newMid - dragStartTouch
                                position = dragStartPosition + delta
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            val upPosition = Offset(event.rawX, event.rawY)
                            val distance = (upPosition - downPosition).getDistance()
                            val duration = event.eventTime - downTime
                            if (distance < 20 && duration < 200) {
                                onSelected(sticker.id)
                            }
                        }

                        MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                            dragStartPosition = position
                            lastScale = scale
                            lastRotation = rotation
                        }
                    }
                    true
                }
        ) {
            val borderModifier = if (isSelected) Modifier.border(1.dp, Color.Black.copy(alpha = 0.2f)) else Modifier

            when (val content = sticker.content) {
                is StickerContent.BitmapSticker -> {
                    Image(
                        bitmap = content.bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .onGloballyPositioned { contentSize = it.size }
                            .then(borderModifier)
                            .size(150.dp)
                    )
                }
                is StickerContent.TextSticker -> {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { contentSize = it.size }
                            .then(borderModifier)
                    ) {
                        androidx.compose.material3.Text(text = content.text, color = Color.Black)
                    }
                }
                is StickerContent.EmojiSticker -> {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { contentSize = it.size }
                            .then(borderModifier)
                    ) {
                        androidx.compose.material3.Text(text = content.emoji, fontSize = 64.sp)
                    }
                }
            }
        }

        if (isSelected) {
            val iconSizeDp = 24.dp
            val iconSizePx = with(density) { iconSizeDp.toPx() }
            val halfIconSizePx = iconSizePx / 2f

            @Composable
            fun iconBox(offset: Offset, onClick: () -> Unit, iconId: Int) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = offset.x - halfIconSizePx
                            translationY = offset.y - halfIconSizePx
                        }
                        .size(iconSizeDp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            iconBox(topLeft, { onDelete(sticker.id) }, R.drawable.sticker_ic_close_white_18dp)
            iconBox(bottomLeft, { isFlipped = !isFlipped }, R.drawable.sticker_ic_flip_white_18dp)

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = bottomRight.x - halfIconSizePx
                        translationY = bottomRight.y - halfIconSizePx
                    }
                    .size(iconSizeDp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .pointerInput(sticker.id) {
                        detectDragGestures { _, dragAmount ->
                            val dragDistance = dragAmount.getDistance() / density.density
                            val scaleChange = dragDistance / 200f
                            scale = (scale + scaleChange).coerceIn(0.3f, 5f)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sticker_ic_scale_white_18dp),
                    contentDescription = "Resize",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
*/

