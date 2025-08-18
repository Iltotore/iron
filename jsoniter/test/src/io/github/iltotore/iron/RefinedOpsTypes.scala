package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Positive

//Opaque types are truly opaque when used in another file than the one where they're defined. See Scala documentation.
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

type Altitude = Altitude.T
object Altitude extends RefinedSubtype[Double, Positive]
