package com.example.myapplication.sticker

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier

@Composable
fun StickerCanvas(
    modifier: Modifier = Modifier,
    stickers: SnapshotStateList<StickerModel>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    onUpdate: (StickerModel) -> Unit,
    onDelete: (String) -> Unit,
) {
    Box(modifier = modifier) {
        stickers.sortedBy { it.zIndex }.forEach { sticker ->
            StickerItem(
                sticker = sticker,
                isSelected = sticker.id == selectedId,
                onSelected = onSelected,
                onUpdate = onUpdate,
                onDelete = onDelete
            )
        }
    }
}


