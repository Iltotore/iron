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
libraryDependencies += "org.tpolecat" %% "skunk-core" % "0.6.1"
```

Mill:

```scala 
ivy"org.tpolecat::skunk-core::0.6.1"
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

// refine a codec implicitly
val a: Query[Void, Username] = sql"SELECT name FROM users".query(varchar)

// refine a codec explictly
val b: Query[Void, Username] = sql"SELECT name FROM users".query(varchar.refined)

// defining a codec for a refined case class
final case class User(name: Username, age: Int :| Positive)
given Codec[User] = (varchar.refined[Not[Blank]] *: int4.refined[Positive]).to[User]

```
