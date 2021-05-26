package io.github.iltotore.iron.constraint

trait Behavior[A, B] {

  inline def assert(value: A): Boolean
}
