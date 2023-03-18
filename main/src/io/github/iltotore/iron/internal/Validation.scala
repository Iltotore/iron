package io.github.iltotore.iron.internal

enum Validation[L, R]:
  case Valid(value: R)
  case Invalid(list: List[L])

import Validation.*

extension [L, R](validation: Validation[L, R])

  def accumulate[L2, R2](other: Validation[L2, R2]): Validation[L | L2, (R, R2)] = (validation, other) match
    case (Valid(value), Valid(otherValue))       => Valid((value, otherValue))
    case (Valid(_), Invalid(otherErrors))        => Invalid(otherErrors)
    case (Invalid(errors), Valid(_))             => Invalid(errors)
    case (Invalid(errors), Invalid(otherErrors)) => Invalid((errors ++ otherErrors).asInstanceOf)

  def map[R2](f: R => R2): Validation[L, R2] = validation match
    case Valid(value)    => Valid(f(value))
    case Invalid(errors) => Invalid(errors)
