---
title: "Borer Support"
---

# Borer Support

This module provides refined types Encoder/Decoder instances for [borer](https://sirthias.github.io/borer/).

## Dependency

SBT: 

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-borer" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-borer:version"
```

### Example Dependencies

SBT:

```scala 
libraryDependencies ++= Seq(
  "io.bullet" %% "borer-core"       % "1.13.0",
  "io.bullet" %% "borer-derivation" % "1.13.0"
)
```

Mill:

```scala 
ivy"io.bullet::borer-core::1.13.0"
ivy"io.bullet::borer-derivation::1.13.0"
```

## How to use

This example shows how to integrate _iron_ with [borer](https://sirthias.github.io/borer/).

After having added the above dependencies you enable the integration layer with this import:

```scala
import io.github.iltotore.iron.borer.given
```

With this in place all refined types `T` automatically have _borer_ `Encoder[T]` and `Decoder[T]`
instances available, as long as the respective Encoders and Decoders for the (unrefined) underlying types are already
`given`.

If a refinement error is triggered during decoding because the decoded value doesn't match the refinement condition(s)
decoding will fail with a `Borer.Error.ValidationFailure`.


Here is a simple example (which can also be found [here](https://github.com/Iltotore/iron/tree/main/examples/borerSerialization)):

```scala 
import io.bullet.borer.{Codec, Json}
import io.bullet.borer.derivation.MapBasedCodecs.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.borer.given // this enables borer <-> iron integration

type Username = (Alphanumeric & MinLength[3] & MaxLength[10]) DescribedAs
  "Username should be alphanumeric and have a length between 3 and 10"

type Password = (Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20]) DescribedAs
  "Password must contain at least a letter, a digit and have a length between 6 and 20"

type Age = Greater[0] DescribedAs
  "Age should be strictly positive"

case class Account(
  name: String :| Username,
  password: String :| Password,
  age: Int :| Age
) derives Codec // relies on the MapBasedCodecs imported above

val account = Account(
  name = "matt",
  password = "bar123",
  age = 42
)

val okValidEncoding = """{"name":"matt","password":"bar123","age":42}"""
val invalidEncoding = """{"name":"matt","password":"bar","age":42}"""

val encoding = Json.encode(account).toUtf8String
assert(encoding == okValidEncoding)

val decoding = Json.decode(okValidEncoding.getBytes).to[Account].valueEither
assert(decoding == Right(account))

val decoding2 = Json.decode(invalidEncoding.getBytes).to[Account].valueEither
decoding2 match {
  case Left(e: Borer.Error.ValidationFailure[_]) =>
    // "Password must contain at least a letter, a digit and have a length between 6 and 20 (input position 26)"
    println(e.getMessage)
  case _ => throw new IllegalStateException
}
```