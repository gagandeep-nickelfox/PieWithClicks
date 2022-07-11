package com.gagandeep.piewithclicks

import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private const val TAG = "TestComposables"

val testTrianglePoints =
    TriangleWithPoints(Coordinate(540F, 540F), Coordinate(1080F, 540F), Coordinate(1080F, 1080F))
val testTrianglePoints2 =
    TriangleWithPoints(Coordinate(540F, 540F), Coordinate(1080F, 540F), Coordinate(1080F, 1080F))

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TriangleTest(rotation: Float, onHit: (Boolean, Float, Float, Float) -> Unit) {

    Box(modifier = Modifier
        .aspectRatio(1F)
        .drawBehind {

            // Draw Outer Rectangle border
            drawRect(color = Color.Green, style = Stroke(width = 5.dp.value))

            Log.d(TAG, "TriangleTest: $size, $center")

            drawCircle(color = Color.Red, radius = 360F, style = Stroke(width = 5F))

            val p1 = Offset(540F, 540F)
            val p2 = Offset(1080F, 540F)
            val p3 = Offset(1080F, 1080F)

//            val rotatedP1: Offset = rotateOffset(p1, center, rotation)
//            val rotatedP2: Offset = rotateOffset(p2, center, rotation)
//            val rotatedP3: Offset = rotateOffset(p3, center, rotation)
//
//            drawCircle(color = Color.Red, radius = 20F, center = rotatedP1)
//            drawCircle(color = Color.Red, radius = 20F, center = rotatedP2)
//            drawCircle(color = Color.Red, radius = 20F, center = rotatedP3)
//
//            drawPath(color = Color.Red, path = Path().apply {
//                moveTo(rotatedP1.x, rotatedP1.y)
//                lineTo(rotatedP2.x, rotatedP2.y)
//                lineTo(rotatedP3.x, rotatedP3.y)
//            })


            rotate(rotation) {

                drawPath(color = Color.Red, path = Path().apply {
                    moveTo(testTrianglePoints.p1.x, testTrianglePoints.p1.y)
                    lineTo(testTrianglePoints.p2.x, testTrianglePoints.p2.y)
                    lineTo(testTrianglePoints.p3.x, testTrianglePoints.p3.y)
                    this.close()
                })

            }.also {
                // Update the rotation off triangle points
                rotateTrianglePoints(rotation, center)

                // Draw Hit Triangle
                drawPath(color = Color.Green.copy(alpha = 0.5F), path = Path().apply {
                    moveTo(testTrianglePoints2.p1.x, testTrianglePoints2.p1.y)
                    lineTo(testTrianglePoints2.p2.x, testTrianglePoints2.p2.y)
                    lineTo(testTrianglePoints2.p3.x, testTrianglePoints2.p3.y)
                    this.close()
                })
            }

        }
        .pointerInteropFilter {
            if (it.action == MotionEvent.ACTION_UP) {
//                Log.d(TAG, "Up Action")
                checkIfHitIsInside(Coordinate(it.x, it.y)).let { hitOcurred ->
                    Log.d(TAG, "Hit was inside triangle -> $hitOcurred")
                    onHit.invoke(hitOcurred, it.x, it.y, rotation)
                }
            }
            return@pointerInteropFilter true
        })
}

fun rotateOffset(originalPoint: Offset, pivot: Offset, rotation: Float): Offset {

    val sineOfAngle = sin(rotation.toRadian())
    val cosOfAngle = cos(rotation.toRadian())

    var x = originalPoint.x - pivot.x
    var y = originalPoint.y - pivot.y

    val rotatedX = x * cosOfAngle - y * sineOfAngle
    val rotatedY = x * sineOfAngle + y * cosOfAngle

    x = rotatedX + pivot.x
    y = rotatedY + pivot.y

    return Offset(x, y)
}

fun rotateTrianglePoints(rotation: Float, pivot: Offset) {
    testTrianglePoints2.p1 = rotatePoint(rotation, testTrianglePoints.p1, pivot)
    testTrianglePoints2.p2 = rotatePoint(rotation, testTrianglePoints.p2, pivot)
    testTrianglePoints2.p3 = rotatePoint(rotation, testTrianglePoints.p3, pivot)
}

fun rotatePoint(rotation: Float, point: Coordinate, pivot: Offset): Coordinate {

    val sineOfAngle = sin(rotation.toRadian())
    val cosOfAngle = cos(rotation.toRadian())

    // Subtract Origin
    val x1 = point.x - pivot.x
    val y1 = point.y - pivot.y

    // Rotate Point
    val xNew = x1 * cosOfAngle - y1 * sineOfAngle
    val yNew = x1 * sineOfAngle + y1 * cosOfAngle

    // Add Origin
    val x2 = xNew + pivot.x
    val y2 = yNew + pivot.y

    return Coordinate(x2, y2)
}

fun checkIfHitIsInside(p: Coordinate): Boolean {
    val p1 = testTrianglePoints2.p1
    val p2 = testTrianglePoints2.p2
    val p3 = testTrianglePoints2.p3

    val area = triangleArea(p1, p2, p3)
    val area1 = triangleArea(p, p2, p3)
    val area2 = triangleArea(p1, p, p3)
    val area3 = triangleArea(p1, p2, p)

    val combined3Area = area1 + area2 + area3

    return (abs(area - combined3Area) < 0.1F)
}

fun triangleArea(p1: Coordinate, p2: Coordinate, p3: Coordinate): Float {
    return abs((p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)) / 2F)
}

@Preview
@Composable
fun TriangleTestPreview() {
    TriangleTest(0F) { hit, x, y, rot ->

    }
}

data class TriangleWithPoints(var p1: Coordinate, var p2: Coordinate, var p3: Coordinate)

data class Coordinate(var x: Float, var y: Float)