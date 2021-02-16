package io.github.iltotore.scalalint.constraint.inlined

class NotInlinedError(value: String) extends Error(s"$value is not inlined")