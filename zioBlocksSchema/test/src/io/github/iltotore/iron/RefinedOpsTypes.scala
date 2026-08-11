package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

type Altitude = Altitude.T
object Altitude extends RefinedSubtype[Double, Positive]
