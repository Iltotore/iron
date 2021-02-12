package io.github.iltotore.scalalint.constraint

class NotInlinedError(value: String) extends Error(s"$value is not inlined")