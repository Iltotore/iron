package io.github.iltotore.iron.constraint

import scala.quoted.*

object macros:

  inline def checkMinLength(inline value: String, inline minLength: Int): Boolean = ${checkMinLengthImpl('value, 'minLength)}

  private def checkMinLengthImpl(expr: Expr[String], lengthExpr: Expr[Int])(using Quotes): Expr[Boolean] =
    (expr.value, lengthExpr.value) match
      case (Some(value), Some(minLength)) => Expr(value.length >= minLength)
      case _ => '{${expr}.length >= $lengthExpr}

  inline def checkMaxLength(inline value: String, inline maxLength: Int): Boolean = ${ checkMaxLengthImpl('value, 'maxLength) }

  private def checkMaxLengthImpl(expr: Expr[String], lengthExpr: Expr[Int])(using Quotes): Expr[Boolean] =
    (expr.value, lengthExpr.value) match
      case (Some(value), Some(maxLength)) => Expr(value.length <= maxLength)
      case _                              => '{ ${ expr }.length <= $lengthExpr }


  inline def checkContain(inline value: String, inline part: String): Boolean = ${checkContainImpl('value, 'part)}

  private def checkContainImpl(expr: Expr[String], partExpr: Expr[String])(using Quotes): Expr[Boolean] =
    (expr.value, partExpr.value) match
      case (Some(value), Some(part)) => Expr(value.contains(part))
      case _ => '{${expr}.contains($partExpr)}


  inline def checkLowerCase(value: String): Boolean =
    ${ checkLowerCaseImpl('value) }

  def checkLowerCaseImpl(valueExpr: Expr[String])(using Quotes): Expr[Boolean] =
    valueExpr.value match
      case Some(value) => Expr(value equals value.toLowerCase)
      case None        => '{ $valueExpr equals $valueExpr.toLowerCase }

  inline def checkUpperCase(value: String): Boolean =
    ${ checkUpperCaseImpl('value) }

  def checkUpperCaseImpl(valueExpr: Expr[String])(using Quotes): Expr[Boolean] =
    valueExpr.value match
      case Some(value) => Expr(value equals value.toUpperCase)
      case None        => '{ $valueExpr equals $valueExpr.toUpperCase }

  inline def checkMatch(value: String, regex: String): Boolean =
    ${ checkMatchImpl('value, 'regex) }

  def checkMatchImpl(valueExpr: Expr[String], regexExpr: Expr[String])(using Quotes): Expr[Boolean] =
    (valueExpr.value, regexExpr.value) match
      case (Some(value), Some(regex)) => Expr(value.matches(regex))
      case _                          => '{ $valueExpr.matches($regexExpr) }
