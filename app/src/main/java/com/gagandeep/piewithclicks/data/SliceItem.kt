package com.gagandeep.piewithclicks.data

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import com.gagandeep.piewithclicks.Coordinate
import com.gagandeep.piewithclicks.TriangleWithPoints
import java.util.*

data class SliceItem(
    var strength: Int = 60,
    var name: String,
    val color: Color,
    var startAngleInDegrees: Float = 0F
) {
    fun getNextStrengthLevel(): Int {
        return when (strength) {
            in 0..0 -> 20
            in 1..20 -> 40
            in 21..40 -> 60
            in 41..60 -> 80
            in 61..80 -> 100
            in 81..100 -> 0
            else -> throw Exception("Strength value not in range 0 to 100")
        }
    }

    val id: UUID = UUID.randomUUID()

    val strengthZeroToOne get() = strength.let {
        0.5F + (((strength - 0) * 0.5F) / 100)
    }

    var triangleWithPoints = TriangleWithPoints(Coordinate(0F, 0F), Coordinate(0F, 0F), Coordinate(0F, 0F))
}
