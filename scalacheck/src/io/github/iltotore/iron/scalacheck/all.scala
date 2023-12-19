package io.github.iltotore.iron.scalacheck

object all extends AnyArbitrary:
  export char.given
  export collection.given
  export numeric.given
  export string.given