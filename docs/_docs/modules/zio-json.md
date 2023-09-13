---
title: "ZIO JSON Support"
---

# ZIO JSON Support

This module provides refined types Encoder/Decoder instances for [ZIO JSON](https://zio.github.io/zio-json/).

## Dependency

SBT: 

```scala sc:nocompile
libraryDependencies += "io.github.iltotore" %% "iron-zio-json" % "version"
```

Mill:

```scala sc:nocompile
ivy"io.github.iltotore::iron-zio-json:version"
```

### Following examples' dependencies

SBT:

```scala sc:nocompile
libraryDependencies += "dev.zio" %% "zio-json" % "0.3.0"
```

Mill:

```scala sc:nocompile
ivy"dev.zio::zio:0.3.0"
```

## Encoder/Decoder instances

Given Encoder/Decoder for Iron enables using refined types for JSON serialization/deserialization:

```scala sc:nocompile
import zio.json.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.zioJson.given

case class User(name: String :| Alphanumeric, age: Int :| Positive)

given JsonCodec[User] = DeriveJsonCodec.gen

//Encoding
User("Iltotore", 8).toJson //{"name":"Iltotore", "age":18}

//Decoding
"""{"name":"Iltotore","age":18}""".fromJson[User] //Right(User(Iltotore, 18))
"""{"name":"Iltotore","age":-18}""".fromJson[User] //Left(.age(Should be greater than 0))
```
