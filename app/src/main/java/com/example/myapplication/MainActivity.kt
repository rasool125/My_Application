package com.example.myapplication


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.sticker.StickerModel
import com.example.myapplication.ui.theme.MyApplicationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val imageStickerBitmap = ImageBitmap.imageResource(R.drawable.sticker_ic_flip_white_18dp)

            val stickers = remember {
                mutableStateListOf<StickerModel>()
            }

            var selectedId by remember { mutableStateOf<String?>(null) }

            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
//                        Column(modifier = Modifier.fillMaxSize()) {
//
//                            // Buttons for adding stickers
//                            Button(onClick = {
//                                stickers.add(
//                                    StickerModel(
//                                        content = StickerContent.BitmapSticker(bitmap = imageStickerBitmap),
//                                        position = Offset(100f, 100f)
//                                    )
//                                )
//                            }) {
//                                Text("Add Image Sticker")
//                            }
//
//                            Button(onClick = {
//                                stickers.add(
//                                    StickerModel(
//                                        content = StickerContent.TextSticker(text = "New Text Sticker"),
//                                        position = Offset(150f, 150f)
//                                    )
//                                )
//                            }) {
//                                Text("Add Text Sticker")
//                            }
//
//                            Button(onClick = {
//                                stickers.add(
//                                    StickerModel(
//                                        content = StickerContent.EmojiSticker(emoji = "🎉"),
//                                        position = Offset(200f, 200f)
//                                    )
//                                )
//                            }) {
//                                Text("Add Emoji Sticker")
//                            }
//
//                            // Sticker canvas
//                            StickerCanvas(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(Color.White),
//                                stickers = stickers,
//                                selectedId = selectedId,
//                                onSelected = { selectedId = it },
//                                onUpdate = { updated ->
//                                    val index = stickers.indexOfFirst { it.id == updated.id }
//                                    if (index != -1) stickers[index] = updated
//                                },
//                                onDelete = { id ->
//                                    stickers.removeAll { it.id == id }
//                                    if (selectedId == id) selectedId = null
//                                }
//                            )
//                        }

                        val dummyItems = List(20) { "Item ${it + 1}" }
                        StickerGridWithPopup(stickerList = dummyItems)
                    }
                }
            }
        }
    }
}




@Composable
fun StickerGridWithPopup(
    stickerList: List<String>
) {
    val density = LocalDensity.current

    var popupSticker by remember { mutableStateOf<String?>(null) }
    var itemBounds by remember { mutableStateOf<Rect?>(null) }
    var showPopup by remember { mutableStateOf(false) }

    val scaleAnim = remember { Animatable(1f) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stickerList) { sticker ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Gray, shape = CircleShape)
                        .onGloballyPositioned { coords ->
                            if (popupSticker == sticker) {
                                val position = coords.positionInRoot()
                                val size = coords.size
                                itemBounds = Rect(
                                    offset = position,
                                    size = Size(size.width.toFloat(), size.height.toFloat())
                                )
                            }
                        }
                        .clickable {
                            popupSticker = sticker
                            showPopup = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sticker, color = Color.White)
                }
            }
        }

        // OVERLAY: Popup that exactly matches original item
        if (showPopup && popupSticker != null && itemBounds != null) {
            val bounds = itemBounds!!
            val widthDp = with(density) { bounds.width.toDp() }
            val heightDp = with(density) { bounds.height.toDp() }
            val offsetXDp = with(density) { bounds.left.toDp() }
            val offsetYDp = with(density) { bounds.top.toDp() }

            LaunchedEffect(popupSticker) {
                scaleAnim.snapTo(1f)
                scaleAnim.animateTo(
                    targetValue = 2f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            showPopup = false
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .absoluteOffset(x = offsetXDp, y = offsetYDp)
                        .size(widthDp, heightDp)
                        .graphicsLayer {
                            scaleX = scaleAnim.value
                            scaleY = scaleAnim.value
                            transformOrigin = TransformOrigin.Center // Very important!
                        }
                        .background(Color.Black, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = popupSticker!!, color = Color.White, fontSize = 24.sp)
                }
            }
        }
    }
}



