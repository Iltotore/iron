package io.github.iltotore.iron

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//Opaque types are truly opaque when used in another file than the one where they're defined. See Scala documentation.
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]

type Moisture = Double :| Positive
object Moisture extends RefinedTypeOps[Moisture]
