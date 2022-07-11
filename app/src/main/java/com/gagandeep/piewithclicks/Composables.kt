package com.gagandeep.piewithclicks

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gagandeep.piewithclicks.data.SliceItem
import kotlin.math.PI
import kotlin.math.abs

@Composable
fun Pie(listOfSlices: List<SliceItem>, backgroundColor: Color, onclick: (SliceItem) -> Unit) {

    val mContext = LocalContext.current

    val rotation = 0F

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF212121))
        .aspectRatio(1F)
        .drawBehind {

            val arcAngle = 360F / listOfSlices.size
            rotate(rotation) {
                listOfSlices.forEach {
                    drawContext.canvas.nativeCanvas.apply {
                        val tf: Typeface =
                            Typeface.createFromAsset(mContext.assets, "fonts/inter_medium.ttf")
                        val paint = android.graphics
                            .Paint()
                            .apply {
                                this.typeface = tf
                                this.fontMetrics
                                this.color = android.graphics.Color.parseColor("#DDC4C4C4")
                                this.textSize = 42F
                                this.letterSpacing = 0.6F
                                this.textAlign = android.graphics.Paint.Align.CENTER
                            }

                        val topLeft = Offset.Zero

                        val path = android.graphics
                            .Path()
                            .apply {
                                this.addArc(
                                    topLeft.x.plus(20F),
                                    topLeft.y.plus(20F),
                                    size.width.minus(20F),
                                    size.height.minus(20F),
                                    it.startAngleInDegrees,
                                    arcAngle
                                )
                            }
                        drawTextOnPath(it.name, path, 0F, 80F, paint)
                    }
                }
            }
        }
        .padding(56.dp)
        .pointerInput(Unit) {
            detectTapGestures(onTap = { offset ->
                listOfSlices
                    .firstOrNull { sliceItem ->
                        checkIfTouchHitTriangle(
                            Coordinate(offset.x, offset.y),
                            sliceItem.triangleWithPoints
                        )
                    }
                    ?.let {
                        onclick(it)
                    }
            })
        },
        onDraw = {

            val arcAngle = 360F / listOfSlices.size

            // Draw Outer Border for Composable
//            drawRect(color = Color.Red, style = Stroke(width = 5.dp.value))

            // Draw Circular Outer Border for Composable
            // drawCircle(color = Color.Red, style = Stroke(width = 5.dp.value))

//            val rotation = arcAngle
//                .div(4)
//                .plus(arcAngle.div(2))

            rotate(rotation) {

                // Draw Slices all over Circle
                listOfSlices.forEach { sliceItem ->

                    val strength = sliceItem.strengthZeroToOne

                    val x = size.width.times((1.0F).minus(strength))
                    val y = size.height.times((1.0F).minus(strength))

                    drawArc(
                        color = sliceItem.color,
                        startAngle = sliceItem.startAngleInDegrees,
                        sweepAngle = arcAngle,
                        useCenter = true,

                        topLeft = Offset(x, y),
                        size = size
                            .times(strength)
                            .offsetSize(Offset(x, y))
                    ).also {
                        // Update the clickable triangle for each Slice Item
                        sliceItem.triangleWithPoints.apply {
                            p1 = Coordinate(center.x, center.y)

                            val startingPoint =
                                Coordinate(size.width, size.minDimension.div(2))

                            p2 = rotatePoint(sliceItem.startAngleInDegrees.plus(rotation), startingPoint, center)

                            p3 = rotatePoint(
                                arcAngle,
                                Coordinate(p2.x, p2.y),
                                center
                            )

                            // TO preview clickable areas

//                            drawCircle(
//                                color = Color.White.copy(alpha = 0.5F),
//                                radius = 30F,
//                                center = Offset(p1.x, p1.y)
//                            )
//                            drawCircle(
//                                color = Color.White.copy(alpha = 0.5F),
//                                radius = 30F,
//                                center = Offset(p2.x, p2.y)
//                            )
//                            drawCircle(
//                                color = Color.White.copy(alpha = 0.5F),
//                                radius = 30F,
//                                center = Offset(p3.x, p3.y)
//                            )
//
//                            drawPath(
//                                color = Color.White.copy(alpha = 0.5F),
//                                path = Path().apply {
//                                    moveTo(p1.x, p1.y)
//                                    lineTo(p2.x, p2.y)
//                                    lineTo(p3.x, p3.y)
//                                    this.close()
//                                })
                        }
                    }
                }
            }

            // Draw Concentric Circles denoting levels
            drawConcentricCircles()

            //Draw Grout
            drawGrout(listOfSlices, backgroundColor, rotation)


        }
    )
}

private fun DrawScope.drawGrout(listOfSlices: List<SliceItem>, color: Color, rotation: Float) {
    rotate(rotation) {
        listOfSlices.forEach {
            rotate(it.startAngleInDegrees) {
                drawLine(
                    color = color,
                    start = center,
                    Offset(size.width.plus(100), size.height.div(2)),
                    strokeWidth = 28F
                )
            }
        }
    }
}

fun checkIfTouchHitTriangle(p: Coordinate, t: TriangleWithPoints): Boolean {
    val p1 = t.p1
    val p2 = t.p2
    val p3 = t.p3

    val area = triangleArea(p1, p2, p3)
    val area1 = triangleArea(p, p2, p3)
    val area2 = triangleArea(p1, p, p3)
    val area3 = triangleArea(p1, p2, p)

    val combined3Area = area1 + area2 + area3

    return (abs(area - combined3Area) < 0.1F)
}

private fun DrawScope.drawConcentricCircles() {

    val lineColor = Color(0xFFC4C4C4)
    val strokeWidth = 1F

    drawCircle(
        color = lineColor,
        radius = size.minDimension
            .div(2)
            .times(1),
        style = Stroke(width = strokeWidth)
    )

    drawCircle(
        color = lineColor,
        radius = size.minDimension
            .div(2)
            .times(0.8F),
        style = Stroke(width = strokeWidth)
    )

    drawCircle(
        color = lineColor,
        radius = size.minDimension
            .div(2)
            .times(0.6F),
        style = Stroke(width = strokeWidth)
    )

    drawCircle(
        color = lineColor,
        radius = size.minDimension
            .div(2)
            .times(0.4F),
        style = Stroke(width = strokeWidth)
    )

    drawCircle(
        color = lineColor,
        radius = size.minDimension
            .div(2)
            .times(0.2F),
        style = Stroke(width = strokeWidth)
    )
}

private fun Size.offsetSize(offset: Offset): Size =
    Size(this.width - offset.x, this.height - offset.y)

@Preview(showBackground = true, backgroundColor = 0xFF212121)
@Composable
fun PiePreview() {
    Pie(listOfSlices = listOf(
        SliceItem(name = "TANNIN", color = Color(0xFFB71D2B)),
        SliceItem(name = "ACIDITY", color = Color(0xFFBCAA2A)),
        SliceItem(name = "BODY", color = Color(0xFFE8A811)),
        SliceItem(name = "ALCOHOL", color = Color(0xFFDF7721)),
        SliceItem(name = "SWEETNESS", color = Color(0xFFE61841)),
    )
        .also {
            val arcAngle: Float = 360F.div(it.size)
            it.onEachIndexed { index, sliceItem ->
                sliceItem.startAngleInDegrees = index * arcAngle
            }
        }, onclick = {}, backgroundColor = Color(0xFF212121)
    )
}

fun Float.toRadian() = (this * (PI / 180)).toFloat()