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

  transparent inline def singleCheck42[T <: String & Singleton](using x: ClassTag[constraint.Match[T]]): T = scala.compiletime.constValue[T]

  transparent inline def extractRegex[T <: Match[_] | DescribedAs[Match[_], _]](): String = {
    ${ extractRegexCode[T]() }
  } 

  def extractRegexCode[T]()(using Quotes, Type[T]): Expr[String] = {
    import quotes.reflect.*
    import io.github.iltotore.iron.string.constraint.Match

    def extractFromConstant(typeRepr: TypeRepr): String = {
      typeRepr match {
        case ConstantType(StringConstant(s)) => s
        case _ => report.throwError(typeRepr.show + " is not string contant")
      }
    }

    val tpe = TypeRepr.of[T]

    val s = tpe.asType match {
      case '[Match[v]] => extractFromConstant(TypeRepr.of[v])
      case '[DescribedAs[Match[v], _]] => extractFromConstant(TypeRepr.of[v])
      case _ => report.throwError(tpe.show + " is not Match or DescribedAs")
    }

    //   // Type.valueOfConstant

    // val r = tpe.asType match 
  
    //   // case '[Map { type Map$K <: AnyRef; type Map$V = Int }] => "asd"
    //   case '[constraint.Match[v] { type Match$V = a } ] => ???
    //   case '[ constraint.Match[String] { type V = q }] => ???
    //   case _ => "fail"
    

    // tpe.

    // w match {
      // case _: constraint.Match[_] => report.error("123")
    // }

    // w match {
      // case '{ constraint.Match[V] { type V = v } } => Type.valueOfConstant[v]
    // }

    // println(tpe.show)

    // report.error("2 " + r)

    Expr(s)
    // println(w)

    // Expr("Abc")

  //   '{ new io.github.iltotore.iron.constraint.Constraint[String, T] {
  //   override def assert(value: String): Boolean = false

  //   override def getMessage(value: String): String = "test"
  // } 
    
  }

}
