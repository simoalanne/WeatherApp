package com.example.weatherapp.utils

/**
 * Returns the interpolation weights for a given step. For example:
 * getInterpolationWeights(1, 3) = (0.33, 0.67)
 *
 * @param currentStep the current step of the interpolation
 * @param gapBetween the difference between start and end values. e.g hours 15:00 - 12:00 = 3
 * @return Pair of weights (startWeight, endWeight) summing to 1.0
 */
fun getInterpolationWeights(currentStep: Int, gapBetween: Int): Pair<Double, Double> {
    val fraction = currentStep.toDouble() / gapBetween.toDouble()
    val endWeight = fraction
    val startWeight = 1.0 - fraction
    return Pair(startWeight, endWeight)
}

