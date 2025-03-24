---
title: "Skunk Support"
---

# Skunk Support

This module provides refined types Codec/Encoder/Decoder instances for [Skunk](https://typelevel.org/skunk).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-skunk" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-skunk:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "org.tpolecat" %% "skunk-core" % "1.0.0-M10"
```

Mill:

```scala 
ivy"org.tpolecat::skunk-core::1.0.0-M10"
```

## Codec instances

Iron provides `Codec` instances for refined types:

```scala 
import skunk.*
import skunk.implicits.*
import skunk.codec.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.skunk.*
import io.github.iltotore.iron.skunk.given

type Username = String :| Not[Blank]

// refining a codec at usage site
val a: Query[Void, Username] = sql"SELECT name FROM users".query(varchar.refined)

// defining a codec for a refined opaque type
opaque type PositiveInt = Int :| Positive
object PositiveInt extends RefinedTypeOps[Int, Positive, PositiveInt]:
  given codec: Codec[PositiveInt] = int4.refined[Positive]

// defining a codec for a refined case class
final case class User(name: Username, age: Int :| Positive)
given Codec[User] = (varchar.refined[Not[Blank]] *: PositiveInt.codec).to[User]
```
