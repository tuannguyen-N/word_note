package com.example.wordnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wordnote.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Slider(
            value = value,
            onValueChange = { newValue ->
                val steppedValue = newValue.roundToInt().toFloat()
                onValueChange(steppedValue)
            },
            valueRange = 1f..4f,
            onValueChangeFinished = onValueChangeFinish,
            steps = 2,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFF756FC6),
                inactiveTrackColor = Color(0xFFE6E6F3)
            ), //needn't because custom color for thumb and track
            thumb = {
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(15.dp)
                        .background(Color.White, shape = CircleShape)
                        .border(2.dp, colorResource(R.color.icon), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(7.dp)
                            .background(
                                colorResource(R.color.icon),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )
                }
            },
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(40.dp))
                        .background(color = Color(0xFFD2CED9))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((value - 1f) / 3f)
                            .background(color = colorResource(R.color.icon))
                    )
                }
            },
            modifier = modifier.height(28.dp)
        )

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "5 words",
                fontFamily = FontFamily(Font(R.font.be_vietnam_pro_medium)),
                color = colorResource(R.color.gray)
            )

            Text(
                text = "10 words",
                fontFamily = FontFamily(Font(R.font.be_vietnam_pro_medium)),
                color = colorResource(R.color.gray)
            )

            Text(
                text = "15 words",
                fontFamily = FontFamily(Font(R.font.be_vietnam_pro_medium)),
                color = colorResource(R.color.gray)
            )

            Text(
                text = "20 words",
                fontFamily = FontFamily(Font(R.font.be_vietnam_pro_medium)),
                color = colorResource(R.color.gray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSeekBar() {
    SeekBar(value = 0.5f, onValueChange = {}, onValueChangeFinish = {})
}