package io.github.iltotore.scalalint.constraint

type AssertionResult = Option[(String, Boolean)]

object AssertionResult {

  inline def Error(inline message: String): AssertionResult = Some((message, true))

  inline def Warn(inline message: String): AssertionResult = Some((message, false))
  
  inline def ensure(value: Boolean)(message: String, error: Boolean = true): AssertionResult = if(value) Some(message, error) else None
}