package io.github.iltotore.iron.string

import constraint.Match
import scala.quoted._
import scala.quoted.ToExpr.ClassToExpr
import scala.deriving.Mirror
import scala.reflect.ClassTag
import io.github.iltotore.iron.constraint.DescribedAs

object compileTime {

  transparent inline def checkLowerCase(value: String): Boolean = {
    ${ checkLowerCaseCode('value) }
  }

  def checkLowerCaseCode(valueExpr: Expr[String])(using q: Quotes): Expr[Boolean] = {
    valueExpr.value match {
      case Some(value) => Expr(value equals value.toLowerCase)
      case None => '{ $valueExpr equals $valueExpr.toLowerCase }
    }
  }

  transparent inline def checkUpperCase(value: String): Boolean = {
    ${ checkUpperCaseCode('value) }
  }

  def checkUpperCaseCode(valueExpr: Expr[String])(using q: Quotes): Expr[Boolean] = {
    valueExpr.value match {
      case Some(value) => Expr(value equals value.toUpperCase)
      case None => '{ $valueExpr equals $valueExpr.toUpperCase }
    }
  }

  transparent inline def checkMatch(value: String, regex: String): Boolean = {
    ${ checkMatchCode('value, 'regex) }
  }

  def checkMatchCode(valueExpr: Expr[String], regexExpr: Expr[String])(using q: Quotes): Expr[Boolean] = {
    valueExpr.value match {
      case Some(value) => 
        //Regex is always const
        val regex = regexExpr.valueOrError.r
        Expr(regex.matches(value))
      case None => '{ $valueExpr.matches($regexExpr) }
    }
  }

}
