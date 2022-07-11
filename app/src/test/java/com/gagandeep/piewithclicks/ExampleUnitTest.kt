package com.gagandeep.piewithclicks

import androidx.compose.ui.geometry.Offset
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun didHit() {
        val testT =
            TriangleWithPoints(
                Coordinate(540F, 540F),
                Coordinate(1080F, 540F),
                Coordinate(1080F, 1080F)
            )

        // Rotate to 130.40463
        val rotationAmount = 332.25443F
//        val rotationAmount = 130.40463F

        testT.p1 = rotatePoint(rotationAmount, testT.p1, pivot = Offset(540F, 540F))
        testT.p2 = rotatePoint(rotationAmount, testT.p2, pivot = Offset(540F, 540F))
        testT.p3 = rotatePoint(rotationAmount, testT.p3, pivot = Offset(540F, 540F))

        val result = checkIfHitIsInsideTest(Coordinate(947.0F, 511.0F), testT)
//        val result = checkIfHitIsInsideTest(Coordinate(295.0F, 693.0F), testT)

        assert(result)
    }

    fun checkIfHitIsInsideTest(p: Coordinate, t: TriangleWithPoints): Boolean {
        val p1 = t.p1
        val p2 = t.p2
        val p3 = t.p3

        val area = triangleArea(p1, p2, p3)
        val area1 = triangleArea(p, p2, p3)
        val area2 = triangleArea(p1, p, p3)
        val area3 = triangleArea(p1, p2, p)

        return (area == area1 + area2 + area3)
    }
}