package io.github.iltotore.iron

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.constraint.collection.Empty

//Opaque types are truly opaque when used in another file than the one where they're defined. See Scala documentation.
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

type EmptySeqDouble = EmptySeqDouble.T
object EmptySeqDouble extends RefinedType[Seq[Double], Empty]
