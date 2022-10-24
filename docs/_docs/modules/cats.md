---
title: "Cats support"
---

This module provides quality of life method for [Cats](https://typelevel.org/cats/). It contains refinement methods using Cats' `Validated`, `ValidatedNec` and `ValidatedNel`.

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

Cats enables accumulative error handling via [Validated](http://typelevel.org/cats/datatypes/validated.html). Iron provides refinement methods that return a `Validated`, `ValidatedNec` or `ValidatedNel` to easily combine runtime refinement with this failure accumulation.

These methods (`refineValidated`, `refineNec`, `refineNel`) are similar to existing `refineEither` and `refineOption`.

The [User example](../reference/refinement.md) now looks like this:

```scala
import io.github.iltotore.iron.*, catsSupport.*, constraint.numeric.{given, *}, constraint.string.{given, *}

case class User(name: String :| Alphanumeric, age: Int :| Greater[0])

def createUserAcc(name: String, age: Int): ValidatedNec[String, User] =
(
    name.refineNec[Username],
    age.refineNec[Age]
).mapN(User.apply)

createUserAcc("Iltotore", 18) //Valid(User(Iltotore,18))
createUserAcc("Il_totore", 18) //Invalid(Chain(Should be alphanumeric))
createUserAcc("Il_totore", -18) //Invalid(Chain(Should be alphanumeric, Should be greater than 0))
```

Or with custom messages:

```scala
type Username = Alphanumeric DescribedAs "Username should be alphanumeric"

type Age = Greater[0] DescribedAs "Age should be positive"

case class User(name: String :| Username, age: Int :| Age)

def createUserAcc(name: String, age: Int): ValidatedNec[String, User] =
(
    name.refineNec[Username],
    age.refineNec[Age]
).mapN(User.apply)

createUserAcc("Iltotore", 18) //Valid(User(Iltotore,18))
createUserAcc("Il_totore", 18) //Invalid(Chain(Username should be alphanumeric))
createUserAcc("Il_totore", -18) //Invalid(Chain(Username should be alphanumeric, Age should be positive))
```