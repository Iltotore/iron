import io.github.iltotore.iron.{*, given}
import io.github.iltotore.iron.constraint.all.*

import io.github.iltotore.iron.zioJson.given

import zio.json.*

type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain atleast a letter, a digit and have a length between 6 and 20"

type Age = Greater[0] DescribedAs "Age should be strictly positive"

/**
 * A basic Account with a name, a password and an age.
 *
 * @param name an alphanumeric String with a length between 3 and 10.
 * @param password an String containing atleast a letter, a digit, with a length between 6 and 20.
 * @param age a strictly positive Int.
 */
case class Account(name: String :| Username, password: String :| Password, age: Int :| Age)

object Account:

  /**
   * Account JSON encoder and decoder.
   */
  given JsonCodec[Account] = DeriveJsonCodec.gen
