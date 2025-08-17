package com.example.myapplication.sticker

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@Composable
fun StickerCanvas(
    modifier: Modifier = Modifier,
    stickers: SnapshotStateList<StickerModel>,
    selectedId: String?,
    onSelected: (String) -> Unit,
    onUpdate: (StickerModel) -> Unit,
    onDelete: (String) -> Unit,
) {
    Box(modifier = modifier
        .pointerInput(Unit) {
            detectTapGestures {
                onSelected("")
            }
        }) {
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



@Composable
fun ImagePickerAndCropper() {
    val imageCropper = rememberImageCropper()
    val cropState = imageCropper.cropState
    val scope = rememberCoroutineScope()

    if (cropState != null) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false // This makes it fullscreen
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .systemBarsPadding()
            ) {
                ImageCropperDialog(state = cropState)
            }
        }
    }




    val context = LocalContext.current


    Button(onClick = {
        scope.launch {
            val result = imageCropper.crop(
                maxResultSize = IntSize(1024, 1024),
                createSrc = {
                    val drawable = ContextCompat.getDrawable(context, R.drawable.image)
                    drawable?.let {
                        val bitmap = drawableToBitmap(it)
                        val imageBitmap = bitmap.asImageBitmap()
                        object : ImageSrc {
                            override val size: IntSize = IntSize(imageBitmap.width, imageBitmap.height)
                            private val resultParams = DecodeParams(1, size.toIntRect())
                            override suspend fun open(params: DecodeParams) = DecodeResult(resultParams, imageBitmap)
                        }
                    }
                }
            )

            when (result) {
                is CropResult.Success -> {
                    val cropped = result.bitmap
                    // Use cropped bitmap here
                }
                is CropError -> {
                    // Handle error
                }
                CropResult.Cancelled -> {
                    // Handle cancel
                }
            }
        }
    }) {
        Text("Crop Drawable Image")
    }

}

