package io.github.iltotore.iron.test.string

import io.github.iltotore.iron.*, constraint.{*, given}, string.constraint.{*, given}
import io.github.iltotore.iron.test.UnitSpec

class CompileTimeSpec extends UnitSpec {

  "A LowerCase constraint" should "return Right if the argument is lower case" in {

    def dummy(x: String / LowerCase): String / LowerCase = x

    "dummy(\"abc\")" should compile 
    "dummy(\"ABC\")" shouldNot compile
  }

  "An UpperCase constraint" should "return Right if the argument is upper case" in {

    def dummy(x: String / UpperCase): String / UpperCase = x

    "dummy(\"ABC\")" should compile 
    "dummy(\"abc\")" shouldNot compile 
  }

  "A custom Match[V] without MatchConstraint" should "always compile" in {

    def dummy(x: String / Match["^[a-z0-9]+"]): String / Match["^[a-z0-9]+"] = x

    "dummy(\"abc123\")" should compile
    "dummy(\"abc\")" should compile
    "dummy(\"123\")" should compile
    "dummy(\" \")" should compile
    "dummy(\"$!#\")" should compile
  }

  "A custom Match[V] with MatchConstraint" should "detect regex at compile time" in {

    type alphanumeric = Match["^[a-z0-9]+"]
    def dummy(x: String / alphanumeric): String / alphanumeric = x
    inline given MatchConstraint[alphanumeric] with {}

    "dummy(\"abc123\")" should compile
    "dummy(\"abc\")" should compile
    "dummy(\"123\")" should compile
    "dummy(\" \")" shouldNot compile
    "dummy(\"$!#\")" shouldNot compile
  }

  "An URLLike constraint" should "return Right if the given String is a valid URL" in {

    def dummy(x: String / URLLike): String / URLLike = x

    """dummy("https://www.example.com")""" should compile
    """dummy("http://invalid.com/perl.cgi?key=|")""" shouldNot compile
  }
}
