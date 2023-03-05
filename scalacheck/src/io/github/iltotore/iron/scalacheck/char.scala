package io.github.iltotore.iron.scalacheck

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.char.*
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Gen.Choose

object char:

  val whitespaceChars: Seq[Char] = (Char.MinValue to Char.MaxValue).filter(_.isWhitespace)

  inline given whitespace: Arbitrary[Char :| Whitespace] = Arbitrary(Gen.oneOf(whitespaceChars)).asInstanceOf

  inline given lowerCase: Arbitrary[Char :| LowerCase] = Arbitrary(Gen.alphaLowerChar).asInstanceOf

  inline given upperCase: Arbitrary[Char :| UpperCase] = Arbitrary(Gen.alphaUpperChar).asInstanceOf

  inline given digit: Arbitrary[Char :| Digit] = Arbitrary(Gen.numChar).asInstanceOf

  inline given letter: Arbitrary[Char :| Letter] = Arbitrary(Gen.alphaChar).asInstanceOf

  private val specialASCIIChars: Seq[Char] = (Char.MinValue until '0') ++ (':' until 'A') ++ ('[' until 'a') ++ ('{' to 127.toChar)

  inline given special: Arbitrary[Char :| Special] = Arbitrary(Gen.oneOf(specialASCIIChars)).asInstanceOf