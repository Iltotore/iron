---
title: "Ciris Support"
---

# Ciris Support

This module provides refined types Encoder/Decoder instances for [Ciris](https://cir.is/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-ciris" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-ciris:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "is.cir" %% "ciris" % "3.1.0"
```

Mill:

```scala 
ivy"is.cir::ciris::3.1.0"
```

## ConfigDecoder instances

Iron provides `ConfigDecoder` instances for refined types:

```scala 
import cats.syntax.all.*
import ciris.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.ciris.given

type Username = String :| (Not[Blank] & MaxLength[32])
type Password = String :| (Not[Blank] & MinLength[9])

case class DatabaseConfig(username: Username, password: Secret[Password])

val databaseConfig: ConfigValue[Effect, DatabaseConfig] = (
  env("DB_USERNAME").as[Username],
  env("DB_PASSWORD").as[Password].secret
).mapN(DatabaseConfig.apply)
```
