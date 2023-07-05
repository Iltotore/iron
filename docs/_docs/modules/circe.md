---
title: "Circe Support"
---

# Circe Support

This module provides refined types Encoder/Decoder instances for [Circe](https://circe.github.io/circe/).

## Dependency

SBT:

```scala
libraryDependencies += "io.github.iltotore" %% "iron-circe" % "version"
```

Mill:

```scala
ivy"io.github.iltotore::iron-circe:version"
```

### Following examples' dependencies

SBT:

```scala
libraryDependencies += "io.circe" %% "circe-core" % "0.14.5"
libraryDependencies += "io.circe" %% "circe-generic" % "0.14.5"
libraryDependencies += "io.circe" %% "circe-parser" % "0.14.5"
```

Mill:

```scala
ivy"io.circe::circe-core::0.14.5"
ivy"io.circe::circe-generic::0.14.5"
ivy"io.circe::circe-parser::0.14.5"
```

## Encoder/Decoder instances

Given Encoder/Decoder for Iron enables using refined types with any Circe feature, including automatic derivation:

```scala
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.circe.given

type Username = Alphanumeric DescribedAs "Username should be alphanumeric"

type Age = Positive DescribedAs "Age should be positive"

case class User(name: String :| Username, age: Int :| Age)

//Encoding
User("Iltotore", 8).asJson //{"name":"Iltotore", "age":18}

//Decoding
decode[User]("""{"name":"Iltotore","age":18}""") //Right(User(Iltotore, 18))
```

Accumulating failures is also supported using `io.circe.parser.decodeAccumulating`:

```scala
decodeAccumulating[User]("""{"name":"Il_totore","age":-18}""")
//Invalid(NonEmptyList(DecodingFailure(Username should only contain alphanumeric characters., List(DownField(name))), DecodingFailure(Age should be positive, List(DownField(age)))))
```
