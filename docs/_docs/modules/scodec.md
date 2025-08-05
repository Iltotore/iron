---
title: "Scodec Support"
---

# Scodec Support

This module provides integration with [Scodec](https://scodec.org/), Scala's combinator library for encoding and decoding binary data.

## Installation

In your build tool, add the following dependency:

```scala
libraryDependencies += "io.github.iltotore" %% "iron-scodec" % "version"
```

## Usage

The `iron-scodec` module provides `Codec` instances for refined types. Simply import the instances and use them as you would with regular scodec codecs:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.scodec.given
import scodec.*
import scodec.bits.*
import scodec.codecs.*

// Use scodec codecs with refined types
val positiveIntCodec: Codec[Int :| Positive] = Codec[Int :| Positive]

// Encoding
val value: Int :| Positive = 42.refine[Positive]
val encoded: Attempt[BitVector] = positiveIntCodec.encode(value)

// Decoding
val decoded: Attempt[DecodeResult[Int :| Positive]] =
  encoded.flatMap(positiveIntCodec.decode)

// Decoding invalid values will fail
val negativeBits = int32.encode(-5).require
val failedDecode = positiveIntCodec.decode(negativeBits) // This will fail
```

## Working with Newtypes

The module also supports Iron's newtypes:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.scodec.given
import scodec.*
import scodec.codecs.*

// Define a newtype
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

// Codec for the newtype
val tempCodec: Codec[Temperature] = Codec[Temperature]

// Usage
val temp = Temperature(25.5)
val encoded = tempCodec.encode(temp)
val decoded = encoded.flatMap(tempCodec.decode)
```

## Scala 3 Derives Support

The module fully supports Scala 3's `derives` syntax for automatic codec derivation:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.constraint.string.MinLength
import io.github.iltotore.iron.scodec.given
import scodec.*

// Define refined types
type Age = Int :| Positive
type Name = String :| MinLength[1]

// Derive codecs for case classes
case class Person(name: Name, age: Age) derives Codec

// The derived codec handles validation automatically
val person = Person("Alice".refine, 25.refine)
val encoded = Codec[Person].encode(person)
val decoded = encoded.flatMap(Codec[Person].decode)
```

## How it Works

The module provides two main given instances:

1. **For newtypes**: Uses `RefinedType.Mirror` to provide codecs for newtypes
2. **For refined types**: Uses the base type's codec and validates the constraint during decoding

When decoding, if the value doesn't satisfy the constraint, the decode operation will fail with an appropriate error message. The integration seamlessly works with scodec's built-in derivation mechanism for case classes and sealed traits.