package io.github.iltotore.iron

import io.github.iltotore.iron.scodec.given
import _root_.scodec.*
import _root_.scodec.bits.*
import _root_.scodec.codecs.*
import io.github.iltotore.iron.constraint.numeric.Positive
import utest.*

object ScodecSuite extends TestSuite:

  val tests: Tests = Tests:
    test("Scodec instances are resolved for Int iron types"):
      val codec = Codec[Int :| Positive]

    test("Scodec instances are resolved for new types"):
      val codec = Codec[Temperature]

    test("Encoding and decoding positive integers"):
      val codec = Codec[Int :| Positive]
      val value: Int :| Positive = 42
      
      val encoded = codec.encode(value)
      assert(encoded.isSuccessful)
      
      val decoded = encoded.flatMap(codec.decode)
      assert(decoded.isSuccessful)
      assert(decoded.require.value == value)

    test("Decoding fails for invalid values"):
      val codec = Codec[Int :| Positive]
      val negativeBits = int32.encode(-5).require
      
      val decoded = codec.decode(negativeBits)
      assert(decoded.isFailure)

    test("Encoding and decoding with Temperature newtype"):
      val codec = Codec[Temperature]
      val temp = Temperature(25.5)
      
      val encoded = codec.encode(temp)
      assert(encoded.isSuccessful)
      
      val decoded = encoded.flatMap(codec.decode)
      assert(decoded.isSuccessful)
      assert(decoded.require.value == temp)

    test("Derives syntax works with refined types"):
      case class Person(name: String, age: Int :| Positive) derives Codec
      
      val person = Person("Alice", 25)
      val codec = Codec[Person]
      
      val encoded = codec.encode(person)
      assert(encoded.isSuccessful)
      
      val decoded = encoded.flatMap(codec.decode)
      assert(decoded.isSuccessful)
      assert(decoded.require.value == person)

    test("Derives syntax works with newtypes"):
      case class WeatherData(location: String, temperature: Temperature) derives Codec
      
      val data = WeatherData("New York", Temperature(20.5))
      val codec = Codec[WeatherData]
      
      val encoded = codec.encode(data)
      assert(encoded.isSuccessful)
      
      val decoded = encoded.flatMap(codec.decode)
      assert(decoded.isSuccessful)
      assert(decoded.require.value == data)