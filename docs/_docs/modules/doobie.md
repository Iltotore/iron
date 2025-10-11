---
title: "Doobie Support"
---

# Doobie Support

This module provides refined types Get/Put/Meta instances for [Doobie](https://tpolecat.github.io/doobie/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-doobie" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-doobie:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "org.tpolecat" %% "doobie-core" % "1.0.0-RC10"
```

Mill:

```scala 
ivy"org.tpolecat::doobie-core::1.0.0-RC10"
```

## Get/Put/Meta instances

```scala
import doobie.*
import doobie.implicits.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.doobie.given

type CountryCode = CountryCode.T
object CountryCode extends RefinedType[Int, Positive]

type CountryName = CountryName.T
object CountryName extends RefinedType[String, Not[Blank]]

type Population = Population.T
object Population extends RefinedType[Int, Positive]

//Refined columns of a table
case class Country(code: CountryCode, name: CountryName, pop: Population)

//Interpolation with refined values
def biggerThan(minPop: Population) =
  sql"""
    select code, name, population
    from country
    where population > $minPop
  """.query[Country]
```

Example inspired by
[another one from Doobie's documentation](https://tpolecat.github.io/doobie/docs/06-Checking.html#checking-a-query).
