package com.example.myapplication.sticker

import androidx.compose.runtime.snapshots.SnapshotStateList

fun bringToFront(stickers: SnapshotStateList<StickerModel>, id: String) {
    val maxZ = stickers.maxOfOrNull { it.zIndex } ?: 0
    val index = stickers.indexOfFirst { it.id == id }
    if (index != -1) stickers[index] = stickers[index].copy(zIndex = maxZ + 1)
}

fun sendToBack(stickers: SnapshotStateList<StickerModel>, id: String) {
    val minZ = stickers.minOfOrNull { it.zIndex } ?: 0
    val index = stickers.indexOfFirst { it.id == id }
    if (index != -1) stickers[index] = stickers[index].copy(zIndex = minZ - 1)
}
