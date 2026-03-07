---
title: "Chimney Support"
---

# Chimney Support

This module provides Transformer/PartialTransformer instances for refined types for [Chimney](https://github.com/scalalandio/chimney).

## Dependency

SBT:

```scala 
libraryDependencies += "io.scalaland" %% "chimney" % "version"
```

Mill:

```scala 
mvn"io.scalaland::chimney:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "io.scalaland" %% "chimney" % "1.8.2"
```

Mill:

```scala 
mvn"io.scalaland::chimney:1.8.2"
```

## Transformer instances

Given Transformer for Iron enables using refined types with automatic derivation:

```scala 
import io.scalaland.chimney.dsl.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.chimney.given

final case class PositiveInt(i: Int :| Positive)
final case class RawInt(i: Int)

PositiveInt(1).transformInto[RawInt].i // 1

RawInt(-1).transformIntoPartial[PositiveInt].asErrorPathMessageStrings // List((i,Should be strictly positive))

RawInt(100).transformIntoPartial[PositiveInt] // Value(PositiveInt(100))

type PureInt = PureInt.T
object PureInt extends RefinedType[Int, Pure]

final case class PureIntW(i: PureInt)

RawInt(1).transformInto[PureIntW].i.value // 1
```
