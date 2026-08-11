---
title: "ZIO Blocks Schema Support"
---

# ZIO Blocks Schema Support

This module automatically provides `zio.blocks.schema.Schema` instances for
Iron refined types, newtypes, and subtypes.

## Dependency

SBT:

```scala
libraryDependencies += "io.github.iltotore" %% "iron-zio-blocks-schema" % "version"
```

Mill:

```scala
mvn"io.github.iltotore::iron-zio-blocks-schema:version"
```

### Following examples' dependencies

SBT:

```scala
libraryDependencies += "dev.zio" %% "zio-blocks-schema" % "0.0.51"
```

Mill:

```scala
mvn"dev.zio::zio-blocks-schema:0.0.51"
```

## Schema instances

Import the instances to obtain schemas for Iron types:

```scala 3
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.zioBlocksSchema.given
import zio.blocks.schema.Schema

type Age = Int :| Positive

val ageSchema = summon[Schema[Age]]
```

When the schema decodes a value, the Iron constraint is checked. Invalid values
produce a `SchemaError` with the constraint message.
