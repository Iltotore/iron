package io.github.iltotore.iron.borerSerialization

import io.bullet.borer.Codec
import io.bullet.borer.derivation.MapBasedCodecs.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.borer.given // this enables borer <-> iron integration

type Username = DescribedAs[
  Alphanumeric & MinLength[3] & MaxLength[10],
  "Username should be alphanumeric and have a length between 3 and 10"
]

type Password = DescribedAs[
  Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20],
  "Password must contain at least a letter, a digit and have a length between 6 and 20"
]

type Age = DescribedAs[Greater[0], "Age should be strictly positive"]

case class Account(
    name: String :| Username,
    password: String :| Password,
    age: Int :| Age
) derives Codec
