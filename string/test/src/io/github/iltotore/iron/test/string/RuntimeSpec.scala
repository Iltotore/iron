package io.github.iltotore.iron.test.string

import io.github.iltotore.iron.*, constraint.{*, given}, string.constraint.{*, given}
import io.github.iltotore.iron.test.UnitSpec

class RuntimeSpec extends UnitSpec {

  "A LowerCase constraint" should "return Right if the argument is lower case" in {

    def dummy(x: String ==> LowerCase): String ==> LowerCase = x

    assert(dummy("abc").isRight)
    assert(dummy("ABC").isLeft)
  }

  "An UpperCase constraint" should "return Right if the argument is upper case" in {

    def dummy(x: String ==> UpperCase): String ==> UpperCase = x

    assert(dummy("ABC").isRight)
    assert(dummy("abc").isLeft)
  }

  "A Matcher[V] constraint" should "return Right if the argument matches the given V regex" in {

    def dummy(x: String ==> Match["^[a-z0-9]+"]): String ==> Match["^[a-z0-9]+"] = x

    assert(dummy("abc123").isRight)
    assert(dummy("abc").isRight)
    assert(dummy("123").isRight)
    assert(dummy(" ").isLeft)
    assert(dummy("$!#").isLeft)
  }

  "An URLLike constraint" should "return Right if the given String is a valid URL" in {

    def dummy(x: String ==> URLLike): String ==> URLLike = x

    //URL samples credits: https://www.regextester.com/94502

    val valid = Seq(
      "https://www.example.com",
      "http://www.example.com",
      "www.example.com",
      "example.com",
      "http://blog.example.com",
      "http://www.example.com/product",
      "http://www.example.com/products?id=1&page=2",
      "http://www.example.com#up",
      "http://255.255.255.255",
      "255.255.255.255",
      "http://www.site.com:8008"
    )

    val invalid = Seq(
      "http://invalid.com/perl.cgi?key=|",
      "http//web-site.com/cgi-bin/perl.cgi?key1=value1&key2"
    )

    for(url <- valid) assert(dummy(url).isRight)
    for(url <- invalid) assert(dummy(url).isLeft)
  }
}
