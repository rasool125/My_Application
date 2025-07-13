package com.example.myapplication


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource

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
                    }
                }
            }
        }
    }
}
