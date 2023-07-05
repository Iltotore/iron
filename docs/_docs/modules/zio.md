---
title: "ZIO Support"
---

# ZIO Support

This module provides refinement methods using [ZIO Prelude](https://zio.dev/zio-prelude/functionalabstractions/)'s `Validation`.

Prelude's typeclass instances already work with [[IronType|io.github.iltotore.iron.IronType]] due to variance.

## Dependency

SBT:

```scala
libraryDependencies += "io.github.iltotore" %% "iron-zio" % "version"
```

Mill:

```scala
ivy"io.github.iltotore::iron-zio:version"
```

### Following examples' dependencies

SBT:

```scala
libraryDependencies += "dev.zio" %% "zio" % "2.0.5"
libraryDependencies += "dev.zio" %% "zio-prelude" % "1.0.0-RC16"
```

Mill:

```scala
ivy"dev.zio::zio:2.0.5"
ivy"dev.zio::zio-prelude:1.0.0-RC16"
```

## Accumulative error handling

ZIO enables accumulative error handling via [Validation](https://zio.dev/zio-prelude/functionaldatatypes/validation/). The ZIO module provides a `refineValidation` method that uses this datatype to handle errors.

This method is similar to `refineEither` and `refineOption` defined in the core module.

The [User example](../reference/refinement.md) now looks like this:


```scala
import zio.prelude.Validation

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.zio.*


type Username = Alphanumeric DescribedAs "Username should be alphanumeric"

type Age = Positive DescribedAs "Age should be positive"

case class User(name: String :| Username, age: Int :| Age)

def createUser(name: String, age: Int): Validation[String, User] =
  Validation.validateWith(
    name.refineValidation[Username],
    age.refineValidation[Age]
  )(User.apply)

createUser("Iltotore", 18) //Success(Chunk(),User(Iltotore,18))
createUser("Il_totore", 18) //Failure(Chunk(),NonEmptyChunk(Username should be alphanumeric))
createUser("Il_totore", -18) //Failure(Chunk(),NonEmptyChunk(Username should be alphanumeric, Age should be positive))
```

## Companion object (RefinedTypeOps validation extension)

Companion object created with `RefinedTypeOps` is being extended by set of functions.

### Companion object
```scala
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]
```

### Imports
`import io.github.iltotore.iron.zio.validation`

### validation
The example below returns `ZValidation.Success` or `ZValidation.Failure`.

```scala
Temperature.validation(x)
```