package com.example.myapplication.sticker

import androidx.compose.ui.graphics.ImageBitmap

sealed class StickerContent {
    data class BitmapSticker(val bitmap: ImageBitmap) : StickerContent()
    data class TextSticker(val text: String) : StickerContent()
    data class EmojiSticker(val emoji: String) : StickerContent() // Optional
}
