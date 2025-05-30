package com.example.weatherapp.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties.Rotation
import ir.ehsannarmani.compose_charts.models.Line
import kotlin.math.ceil
import kotlin.math.floor
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import ir.ehsannarmani.compose_charts.models.DotProperties
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.GridProperties.AxisProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties

/**
 * Composable for displaying a temperature chart of daily weathers min, max and average temperatures.
 * It uses the compose-charts library to do so.
 *
 * @param dailyMinTemps The list of daily minimum temperatures.
 * @param dailyMaxTemps The list of daily maximum temperatures.
 * @param dailyMeanTemps The list of daily mean temperatures.
 * @param weekDays The list of week days.
 * @param tempUnit The temperature unit.
 */
@Composable
fun TemperatureChart(
    dailyMinTemps: List<Double>,
    dailyMaxTemps: List<Double>,
    dailyMeanTemps: List<Double>,
    weekDays: List<String>,
    tempUnit: String
) {
    val coldestLabel = stringResource(R.string.coldest)
    val hottestLabel = stringResource(R.string.hottest)
    val averageLabel = stringResource(R.string.average)

    // lowest value should be the first dividable by 5 floored down
    val minValue = floor(dailyMinTemps.min() / 5) * 5
    // highest value should be the first dividable by 5 rounded up
    val maxValue = ceil(dailyMaxTemps.max() / 5) * 5
    val totalIntervals = ((maxValue - minValue) / 5).toInt() + 1

    // indicators go with 5 degree increments. 5 because that has a good balance
    // between readability and providing not too much or too little indicators
    val indicators = List(totalIntervals) {
        minValue + it * 5
    }.reversed()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(Color(0f, 0f, 0f, 0.3f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                stringResource(R.string.daily_temperatures_chart_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = Bold,
                color = Color.White
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 0.5f.dp)
        }
        LineChart(
            modifier = Modifier
                .fillMaxSize()
                .height(300.dp),
            data = remember {
                listOf(
                    Line(
                        label = hottestLabel,
                        values = dailyMaxTemps,
                        color = SolidColor(Color(255, 183, 77, 255)),
                        firstGradientFillColor = Color(255, 183, 77, 200),
                        secondGradientFillColor = Color(255, 183, 77, 100),
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White)
                        ),
                    ),
                    Line(
                        label = averageLabel,
                        values = dailyMeanTemps,
                        color = SolidColor(Color(76, 175, 80, 255)),
                        firstGradientFillColor = Color(76, 175, 80, 200),
                        secondGradientFillColor = Color(76, 175, 80, 100),
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White)
                        )
                    ),
                    Line(
                        label = coldestLabel,
                        values = dailyMinTemps,
                        color = SolidColor(Color(30, 136, 229, 255)),
                        firstGradientFillColor = Color(30, 136, 229, 200),
                        secondGradientFillColor = Color(30, 136, 229, 100),
                        dotProperties = DotProperties(
                            enabled = true,
                            color = SolidColor(Color.White)
                        )
                    ),
                )
            },
            indicatorProperties = HorizontalIndicatorProperties(
                textStyle = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.White
                ),
                contentBuilder = {
                    "$it $tempUnit"
                },
                indicators = indicators
            ),
            minValue = minValue.toDouble(),
            maxValue = maxValue.toDouble(),
            labelProperties = LabelProperties(
                labels = weekDays,
                enabled = true,
                textStyle = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.White
                ),
                rotation = Rotation(degree = 0f)
            ),
            labelHelperProperties = LabelHelperProperties(
                textStyle = TextStyle.Default.copy(
                    fontSize = 12.sp,
                    color = Color.White
                )
            ),
            gridProperties = GridProperties(
                enabled = true,
                xAxisProperties = AxisProperties(
                    enabled = true,
                    color = SolidColor(Color.White.copy(alpha = 0.5f)),
                    lineCount = totalIntervals
                ),
                yAxisProperties = AxisProperties(
                    enabled = true,
                    color = SolidColor(Color.White.copy(alpha = 0.5f)),
                    lineCount = weekDays.size
                )
            ),
            animationMode = AnimationMode.Together(delayBuilder = {
                it * 500L
            }),
        )
    }
}
