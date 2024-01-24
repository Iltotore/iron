package io.github.iltotore.iron.formBorer

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.{*, given}
import io.github.iltotore.iron.borer.given // this enables borer <-> iron integration

type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain at least a letter, a digit and have a length between 6 and 20"

type Age = Greater[0] DescribedAs
  "Age should be strictly positive"

case class Account(
    name: String :| Username,
    password: String :| Password,
    age: Int :| Age
)

object Account:
  given Codec[Account] = MapBasedCodecs.deriveCodec
