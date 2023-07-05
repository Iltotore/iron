---
title: "Jsoniter Support"
---

# Jsoniter Support

This module provides refined types codec instances for [Jsoniter Scala](https://github.com/plokhotnyuk/jsoniter-scala).

## Dependency

SBT: 

```scala
libraryDependencies += "io.github.iltotore" %% "iron-jsoniter" % "version"
```

Mill:

```scala
ivy"io.github.iltotore::iron-jsoniter:version"
```

### Following examples' dependencies

SBT:

```scala
libraryDependencies += "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.19.1"
libraryDependencies += "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.19.1"
```

Mill:

```scala
ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core:2.19.1"
ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:2.19.1"
```

## Encoder/Decoder instances

Given `JsonValueCodec` for Iron enables using refined types for JSON serialization/deserialization:

```scala
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.github.plokhotnyuk.jsoniter_scala.core.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.jsoniter.given

case class User(name: String :| Alphanumeric, age: Int :| Positive)

given JsonValueCodec[User] = JsonCodecMaker.make

//Encoding
writeToString[User](User("totore", 18))

//Decoding
readFromString[User]("""{"name":"totore","age":18}""")
readFromString[User]("""{"name":"totore","age":-18}""") //Error: "Should be positive"
```
