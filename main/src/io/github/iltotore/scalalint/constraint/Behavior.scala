package io.github.iltotore.scalalint.constraint

trait Behavior[A, B] {

  inline def assert(value: A): Boolean
}
