package io.github.iltotore.iron.constraint

trait Consequence[A, B1, B2] {
  
  inline def assert(value: A): Boolean

  inline def getMessage(value: A): String
}

object Consequence {

  /**
   * An always-valid consequence.
   * @tparam A the input type
   * @tparam B1
   * @tparam B2
   */
  class VerifiedConsequence[A, B1, B2] extends Consequence[A, B1, B2] {

    override inline def assert(value: A): Boolean = true

    override inline def getMessage(value: A): String = "valid"
  }

  inline def verified[A, B1, B2]: VerifiedConsequence[A, B1, B2] = new VerifiedConsequence
  
  
  class InvalidConsequence[A, B1, B2] extends Consequence[A, B1, B2] {

    override inline def assert(value: A): Boolean = false

    override inline def getMessage(value: A): String = "invalid"
  }
  
  inline def invalid[A, B1, B2]: InvalidConsequence[A, B1, B2] = new InvalidConsequence
}