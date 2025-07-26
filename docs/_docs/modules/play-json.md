---
title: "play JSON Support"
---

# play JSON Support

This module provides refined types Writes/Reads instances for [play JSON](https://github.com/playframework/play-json/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-play-json" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-play-json:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "org.playframework" %% "play-json" % "3.0.5"
```

Mill:

```scala 
ivy"org.playframework::play-json::3.0.5"
```

## Writes/Reads instances

```scala 
import play.api.libs.json.{Reads, Writes, Json}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.playJson.given

type Username = DescribedAs[Alphanumeric, "Username should be alphanumeric"]

type Age = DescribedAs[Positive, "Age should be positive"]

case class User(name: String :| Username, age: Int :| Age)

//Encoding
Json.stringify(Json.writes[User].writes(User("Iltotore", 8))) //{"name":"Iltotore", "age":18}

//Decoding
Json.fromJson[User](Json.parse("""{"name":"Iltotore","age":18}"""))(Json.reads[User]) //JsSuccess(User(Iltotore,18),)
```
