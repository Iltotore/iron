---
title: "uPickle Support"
---

# uPickle Support

This module provides refined types Writer/Reader instances for [uPickle](https://com-lihaoyi.github.io/upickle/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-upickle" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-upickle:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "com.lihaoyi" %% "upickle" % "3.1.3"
```

Mill:

```scala 
ivy"com.lihaoyi::upickle:3.1.3"
```

## Writer/Reader instances

You can serialize and deserialize refined values using Iron's Writer/Reader instances for refined types.

```scala 
import upickle.default._
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.upickle.given

opaque type Username = String :| Alphanumeric
object Username extends RefinedTypeOps[String, Alphanumeric, Username]

opaque type Age = Int :| Positive
object Age extends RefinedTypeOps[Int, Positive, Age]

case class User(name: Username, age: Age) derives ReadWriter

write(User("Iltotore", 19)) //{"name":"Iltotore","age":19}

read[User]("""{"name":"Iltotore","age":19}""") //User("Iltotore", 19)
read[User]("""{"name":"Iltotore","age":-19}""") //AbortException: Should be strictly positive
read[User]("""{"name":"Il_totore","age":19}""") //AbortException: Should be alphanumeric
```