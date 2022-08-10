package io.github.iltotore.iron

trait Theorem[A, C1, C2]:

  inline def test(value: A): Boolean

  inline def message: String

object Theorem:

  private class Axiom[A, C1, C2, R <: Boolean & Singleton, M <: String & Singleton](val result: R, val msg: M) extends Theorem[A, C1, C2]:

    override inline def test(value: A): Boolean = result

    override inline def message: String = msg

  transparent inline def valid[A, C1, C2]: Theorem[A, C1, C2] = new Axiom(true, "Always valid")

  transparent inline def invalid[A, C1, C2](message: String): Theorem[A, C1, C2] = new Axiom(false, message)
