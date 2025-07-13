package com.example.myapplication.sticker

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID


data class StickerModel(
    val id: String = UUID.randomUUID().toString(),
    val content: StickerContent,
    val position: Offset = Offset(100f, 100f),
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val isFlipped: Boolean = false,
    var zIndex: Int = 0
)



