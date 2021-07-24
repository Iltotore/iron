package io.github.iltotore.iron.constraint

/**
 * Represents a type-level assertion failure.
 * @param input the invalid value
 * @param message the error message
 * @tparam A the input type
 */
case class IllegalValueError[A](input: A, message: String) extends Error(message) {

  def toField(name: String): IllegalValueError.Field = IllegalValueError.Field(name, message)
}

object IllegalValueError {

  /**
   * A field-based variant of [[IllegalValueError]]. Useful for API requests/forms.
   * @param name the field name
   * @param message the error message
   */
  case class Field(name: String, message: String) extends Error(message)
}