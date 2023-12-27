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
libraryDependencies += "org.tpolecat" %% "doobie-core" % "1.0.0-RC4"
```

Mill:

```scala 
ivy"org.tpolecat::doobie-core::1.0.0-RC4"
```

## Get/Put/Meta instances

```scala
import doobie.*
import doobie.implicits.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.doobie.given

opaque type CountryCode = Int :| Positive
object CountryCode extends RefinedTypeOps[Int, Positive, CountryCode]

opaque type CountryName = String :| Not[Blank]
object CountryName extends RefinedTypeOps[String, Not[Blank], CountryName]

opaque type Population = Int :| Positive
object Population extends RefinedTypeOps[Int, Positive, Population]

//Refined columns of a table
case class Country(code: CountryCode, name: CountryName, pop: Population)

//Interpolation with refined values
def biggerThan(minPop: Population) =
  sql"""
    select code, name, population, indepyear
    from country
    where population > $minPop
  """.query[Country] 
```

Example inspired by
[another one from Doobie's documentation](https://tpolecat.github.io/doobie/docs/06-Checking.html#checking-a-query).