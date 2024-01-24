package io.github.iltotore.iron

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

/**
 * We declare test types here, in a separate file, in order to make the opaque aliases truly opaque.
 */
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]

type Moisture = Double :| Positive
object Moisture extends RefinedTypeOps.Transparent[Moisture]
