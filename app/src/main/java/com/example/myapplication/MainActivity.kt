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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
                        PopOutItemInLazyGrid()
                    }
                }
            }
        }
    }
}





@Composable
fun PopOutItemInLazyGrid() {
    val items = List(20) { "Item $it" }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(items) { index, item ->
            val isSelected = selectedIndex == index

            // Animate scale
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1f,
                animationSpec = tween(durationMillis = 200),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin.Center
                    }
                    .zIndex(if (isSelected) 1f else 0f) // Ensure selected item is drawn on top
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Color.Yellow else Color.LightGray)
                    .clickable {
                        selectedIndex = if (isSelected) null else index
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(item)
            }
        }
    }
}




