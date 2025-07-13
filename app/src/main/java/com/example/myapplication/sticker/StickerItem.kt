package com.example.myapplication.sticker

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
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

    // Dynamically determine size for text, emoji, or bitmap
    var contentSize by remember { mutableStateOf(IntSize(250, 250)) }

    val center: Offset
    val sizePx = with(density) { contentSize.width.toFloat() }

    center = Offset(position.x + sizePx / 2f, position.y + contentSize.height / 2f)

    fun transformedCorner(offsetX: Float, offsetY: Float): Offset {
        val scaledX = offsetX * scale
        val scaledY = offsetY * scale
        val radians = Math.toRadians(rotation.toDouble())
        val cos = cos(radians).toFloat()
        val sin = sin(radians).toFloat()

        val rotatedX = scaledX * cos - scaledY * sin
        val rotatedY = scaledX * sin + scaledY * cos

        return center + Offset(rotatedX, rotatedY)
    }

    val topLeft = transformedCorner(-contentSize.width / 2f, -contentSize.height / 2f)
    val bottomLeft = transformedCorner(-contentSize.width / 2f, contentSize.height / 2f)
    val bottomRight = transformedCorner(contentSize.width / 2f, contentSize.height / 2f)

    // Update sticker when properties change
    LaunchedEffect(position, scale, rotation, isFlipped) {
        onUpdate(
            sticker.copy(
                position = position,
                scale = scale,
                rotation = rotation,
                isFlipped = isFlipped
            )
        )
    }

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
                .pointerInput(sticker.id) {
                    detectTransformGestures { centroid, pan, zoom, rotate ->
                        // Calculate movement compensation based on scale and rotation
                        val radians = Math.toRadians(rotation.toDouble())
                        val cos = cos(radians).toFloat()
                        val sin = sin(radians).toFloat()

                        // Adjust pan based on both scale and rotation
                        val adjustedPanX = (pan.x * cos + pan.y * sin)
                        val adjustedPanY = (-pan.x * sin + pan.y * cos)


                        position += Offset(adjustedPanX, adjustedPanY)
                        scale = (scale * zoom).coerceIn(0.3f, 5f)
                        rotation += rotate
                    }
                }
                .pointerInput(sticker.id) {
                    detectTapGestures { onSelected(sticker.id) }
                }
        ) {

            val borderModifier = if (isSelected) Modifier.border(1.dp, Color.Black.copy(alpha = 0.2f)) else Modifier

            when (val content = sticker.content) {
                is StickerContent.BitmapSticker -> {
                    val targetWidth = 150.dp
                    val targetHeight = 150.dp

                    Image(
                        bitmap = content.bitmap,
                        contentDescription = null,
                        modifier = Modifier
                            .onGloballyPositioned {
                                contentSize = it.size
                            }
                            .then(borderModifier)
                            .size(targetWidth, targetHeight)
                    )
                }

                is StickerContent.TextSticker -> {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned {
                                contentSize = it.size
                            }
                            .then(borderModifier)
                    ) {
                        androidx.compose.material3.Text(
                            text = content.text,
                            color = Color.Black,
                        )
                    }
                }

                is StickerContent.EmojiSticker -> {
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned {
                                contentSize = it.size
                            }
                            .then(borderModifier)
                    ) {
                        androidx.compose.material3.Text(
                            text = content.emoji,
                            fontSize = 64.sp
                        )
                    }
                }
            }
        }

        if (isSelected) {
            val iconSizeDp = 24.dp
            val iconSizePx = with(density) { iconSizeDp.toPx() }
            val halfIconSizePx = iconSizePx / 2f

            // ❌ DELETE
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = topLeft.x - halfIconSizePx
                        translationY = topLeft.y - halfIconSizePx
                    }
                    .size(iconSizeDp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { onDelete(sticker.id) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sticker_ic_close_white_18dp),
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 🔁 FLIP
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = bottomLeft.x - halfIconSizePx
                        translationY = bottomLeft.y - halfIconSizePx
                    }
                    .size(iconSizeDp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable {
                        isFlipped = !isFlipped
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sticker_ic_flip_white_18dp),
                    contentDescription = "Flip",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 🔍 RESIZE
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
                        var initialScale = scale
                        var initialDistance = 0f
                        detectDragGestures(
                            onDragStart = { offset ->
                                initialScale = scale
                                initialDistance = offset.getDistance()
                            },
                            onDrag = { change, _ ->
                                val currentDistance = change.position.getDistance()
                                val scaleFactor = currentDistance / initialDistance
                                scale = (initialScale * scaleFactor).coerceIn(0.3f, 5f)
                            }
                        )
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



