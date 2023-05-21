---
title: "Cats Support"
---

# Cats Support

This module provides typeclass instances for [Cats](https://typelevel.org/cats/). Furthermore, it contains refinement methods using Cats' `Validated`, `ValidatedNec` and `ValidatedNel`.

## Dependency

SBT:

```scala
libraryDependencies += "io.github.iltotore" %% "iron-cats" % "version"
```

Mill:

```scala
ivy"io.github.iltotore::iron-cats:version"
```

## Accumulative error handling

Cats enables accumulative error handling via [Validated](http://typelevel.org/cats/datatypes/validated.html). Iron provides refinement methods that return an `Either`, `EitherNec` or `EitherNel` to easily combine runtime refinements with failure accumulation. There are also variants that return a `Validated`, `ValidatedNec` or `ValidatedNel`.

These methods are similar to `refineEither` and `refineOption` defined in the core module.

The [User example](../reference/refinement.md) now looks like this:

```scala
import cats.data.EitherNec
import cats.syntax.all.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.*
import io.github.iltotore.iron.constraint.all.*

case class User(name: String :| Alphanumeric, age: Int :| Positive)

def createUserAcc(name: String, age: Int): EitherNec[String, User] =
(
    name.refineNec[Username],
    age.refineNec[Age]
).parMapN(User.apply)

createUserAcc("Iltotore", 18) //Right(User(Iltotore,18))
createUserAcc("Il_totore", 18) //Left(Chain(Should be alphanumeric))
createUserAcc("Il_totore", -18) //Left(Chain(Should be alphanumeric, Should be greater than 0))
```

Or with custom messages:

```scala
type Username = Alphanumeric DescribedAs "Username should be alphanumeric"

type Age = Positive DescribedAs "Age should be positive"

case class User(name: String :| Username, age: Int :| Age)

def createUserAcc(name: String, age: Int): EitherNec[String, User] =
(
    name.refineNec[Username],
    age.refineNec[Age]
).parMapN(User.apply)

createUserAcc("Iltotore", 18) //Right(User(Iltotore,18))
createUserAcc("Il_totore", 18) //Left(Chain(Username should be alphanumeric))
createUserAcc("Il_totore", -18) //Left(Chain(Username should be alphanumeric, Age should be positive))
```

Leveraging typeclass instances via Cats' syntax.

```scala
import io.github.iltotore.iron.cats.given

val name1: String :| Alphanumeric = "Martin"
val name2: String :| Alphanumeric = "George"
val age1: Int :| Greater[0] = 60

name1.show // Martin
name1 |+| name2 // MartinGeorge
age1 === 49 // false
```

## Companion object (RefinedTypeOps extensions)

Companion object created with `RefinedTypeOps` is being extended by set of functions.

### Companion object
```scala
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]
```

### Imports
`import io.github.iltotore.iron.cats.*`

### functions
All the example return cats structures with either result or error report.
- `Temperature.eitherNec(-5.0)`
- `Temperature.eitherNel(-5.0)`
- `Temperature.validated(-5.0)`
- `Temperature.validatedNec(-5.0)`
- `Temperature.validatedNel(-5.0)`
