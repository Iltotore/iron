package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Positive

opaque type Temperature = Int :| Positive
object Temperature extends RefinedTypeOps[Int, Positive, Temperature]
