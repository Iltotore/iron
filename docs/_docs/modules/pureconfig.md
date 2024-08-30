---
title: "PureConfig Support"
---

# PureConfig Support

This module provides refined types ConfigReader instances for [PureConfig](https://pureconfig.github.io/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-pureconfig" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-pureconfig:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "com.github.pureconfig" %% "pureconfig-core" % "0.17.7"
```

Mill:

```scala 
ivy"com.github.pureconfig::pureconfig-core::0.17.7"
```

## ConfigReader instances

Iron provides `ConfigReader` instances for refined types:

```scala 
package io.github.iltotore.iron

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.pureconfig.given

opaque type Username = String :| MinLength[5]
object Username extends RefinedTypeOps[String, MinLength[5], Username]

case class IronTypeConfig(
  username: String :| MinLength[5]
) derives ConfigReader

case class NewTypeConfig(
  username: Username
) derives ConfigReader
```
