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

## Durable validation metadata

By default, constraints with an exactly equivalent ZIO Blocks validation are
embedded in the schema. This survives conversion to `DynamicSchema` (including
its dynamic representation), so validators that load the schema later enforce
the same supported rule. Iron still validates every constraint while decoding;
unsupported constraints are not represented as weaker validation rules.

The built-in translation covers positive/negative numeric constraints, string
regular expressions, and string length constraints. You can provide a
`ZioBlocksValidation[A, C]` given to translate an application-specific
constraint as well.

```scala 3
import io.github.iltotore.iron.ZioBlocksValidation
import io.github.iltotore.iron.constraint.any.StrictEqual
import zio.blocks.schema.Validation

given ZioBlocksValidation[Int, StrictEqual[42]] with
  def validation = Some(Validation.Numeric.Set(Set(42)))
```

Iron's validation message is stored on the resulting schema as
`Modifier.config("iron.validation.message", message)`. Both choices can be
controlled with a local given:

```scala 3
import io.github.iltotore.iron.{ZioBlocksSchemaConfig, zioBlocksSchema}
import zioBlocksSchema.given

given ZioBlocksSchemaConfig = ZioBlocksSchemaConfig(
  validation = ZioBlocksSchemaConfig.ValidationEncoding.RuntimeOnly,
  messageMetadata = ZioBlocksSchemaConfig.MessageMetadata.Omit
)
```

`RuntimeOnly` turns off validation-AST embedding but does not turn off Iron's
decode-time validation.
