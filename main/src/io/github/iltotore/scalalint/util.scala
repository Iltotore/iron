package io.github.iltotore.scalalint

object util {

  def summonOption[T <: AnyRef](using value: T = null): Option[T] = Option(value)
}
