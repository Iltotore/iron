package io.github.iltotore.iron.testing

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import scala.annotation.targetName

//Opaque types are truly opaque when used in another file than the one where they're defined. See Scala documentation.
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]:

  extension (self: Temperature)
    @targetName("plusTemperature")
    def +(other: Temperature): Temperature =
      Temperature.applyUnsafe(self.value + other.value)

    @targetName("plusDouble")
    def +(other: Double): Temperature =
      Temperature.applyUnsafe(self.value + other)

type Altitude = Altitude.T
object Altitude extends RefinedSubtype[Double, Positive]:

  extension (self: Altitude)
    @targetName("plusAltitude")
    def +(other: Altitude): Altitude =
      Altitude.applyUnsafe(self.value + other.value)

    @targetName("plusDouble")
    def +(other: Double): Altitude =
      Altitude.applyUnsafe(self.value + other)
