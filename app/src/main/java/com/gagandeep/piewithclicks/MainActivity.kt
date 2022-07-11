package com.gagandeep.piewithclicks

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gagandeep.piewithclicks.data.SliceItem
import com.gagandeep.piewithclicks.ui.theme.PieWithClicksTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val list = remember {
                mutableStateListOf(
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
                    }
            }

            PieWithClicksTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize().background(color = Color(0xFF212121)),
                    color = MaterialTheme.colors.background
                ) {

                    Pie(listOfSlices = list, backgroundColor = Color(0xFF212121)) { sliceItem ->

                        val index = list.indexOf(sliceItem)

                        val newStrength = sliceItem.getNextStrengthLevel()

                        ValueAnimator.ofInt(sliceItem.strength, newStrength).apply {
                            duration = 200
                            addUpdateListener {
                                list[index] = sliceItem.copy(strength = it.animatedValue as Int)
                            }
                            this.interpolator = AccelerateInterpolator()
                        }.start()

                    }

/*                    var rotation by remember {
                        mutableStateOf(0F)
                    }

                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                        TriangleTest(rotation) { hit, x, y, rot ->
                            Toast.makeText(mContext, "Hit: $hit | x:$x, y:$y, rot:$rot ", Toast.LENGTH_SHORT).show()
                        }
                        Slider(
                            value = rotation, onValueChange = {
                                rotation = it
                            }, modifier = Modifier.padding(
                                horizontal = 8.dp,
                            ), valueRange = 0F..360F
                        )
                    }*/
                }
            }
        }
    }
}