package io.github.iltotore.scalalint

package object constraint {

  opaque type Constrained[A, B] <: A = A
  type ==>[A, B] = Constrained[A, B]

  def Constrained[A, B](value: A): Constrained[A, B] = value
}