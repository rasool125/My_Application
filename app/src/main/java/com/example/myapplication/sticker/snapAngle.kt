package com.example.myapplication.sticker

import kotlin.math.roundToInt

fun snapAngle(angle: Float, snapInterval: Float = 15f): Float {
    return (angle / snapInterval).roundToInt() * snapInterval
}
