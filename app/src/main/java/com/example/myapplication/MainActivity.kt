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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.myapplication.sticker.StickerCanvas
import com.example.myapplication.sticker.StickerContent
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
                        Column(modifier = Modifier.fillMaxSize()) {

                            // Buttons for adding stickers
                            Button(onClick = {
                                stickers.add(
                                    StickerModel(
                                        content = StickerContent.BitmapSticker(bitmap = imageStickerBitmap),
                                        position = Offset(100f, 100f)
                                    )
                                )
                            }) {
                                Text("Add Image Sticker")
                            }

                            Button(onClick = {
                                stickers.add(
                                    StickerModel(
                                        content = StickerContent.TextSticker(text = "New Text Sticker"),
                                        position = Offset(150f, 150f)
                                    )
                                )
                            }) {
                                Text("Add Text Sticker")
                            }

                            Button(onClick = {
                                stickers.add(
                                    StickerModel(
                                        content = StickerContent.EmojiSticker(emoji = "🎉"),
                                        position = Offset(200f, 200f)
                                    )
                                )
                            }) {
                                Text("Add Emoji Sticker")
                            }

                            // Sticker canvas
                            StickerCanvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White),
                                stickers = stickers,
                                selectedId = selectedId,
                                onSelected = { selectedId = it },
                                onUpdate = { updated ->
                                    val index = stickers.indexOfFirst { it.id == updated.id }
                                    if (index != -1) stickers[index] = updated
                                },
                                onDelete = { id ->
                                    stickers.removeAll { it.id == id }
                                    if (selectedId == id) selectedId = null
                                }
                            )
                        }

//                        val dummyItems = List(20) { "Item ${it + 1}" }
//                        PopOutItemInLazyGrid()

//                        RotatingBoxExample()

//                        FullScreenExitDialog(onDismiss = {
//
//                        },
//                            onConfirmExit = {
//
//                            })
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


@Composable
fun RotatingBoxExample() {
    var rotationAngle by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Rotatable View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f) // 70% of screen height
                .graphicsLayer {
                    rotationZ = rotationAngle
                }
                .background(Color.Blue)
        ){
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to Rotate
        Button(onClick = {
            rotationAngle += 90f  // Increment by 45 degrees
        }) {
            Text("Rotate")
        }
    }
}





@Composable
fun FullScreenExitDialog(
    onDismiss: () -> Unit,
    onConfirmExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)) // Dimmed background
            .clickable(onClick = onDismiss), // Dismiss when tapping outside
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Exit App?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Are you sure you want to exit the application?",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onConfirmExit,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Exit")
                    }
                }
            }
        }
    }
}
