---
title: "Decline Support"
---

# Skunk Support

This module provides refined types Argument instances for [Decline](https://ben.kirw.in/decline/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-decline" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-decline:version"
```

### Following examples' dependencies

SBT:

```scala 
libraryDependencies += "com.monovore" %% "decline" % "2.4.1"
```

Mill:

```scala 
ivy"com.monovore::decline::2.4.1"
```

## Argument instances

Iron provides `Argument` instances for refined types:

```scala 
import cats.implicits.*
import com.monovore.decline.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.decline.given

type Person = String :| Not[Blank]

opaque type PositiveInt <: Int = Int :| Positive
object PositiveInt extends RefinedTypeOps[Int, Positive, PositiveInt]

object HelloWorld extends CommandApp(
  name = "hello-world",
  header = "Says hello!",
  main = {
    // Defining an option for a constrainted type
    val userOpt =
      Opts.option[Person]("target", help = "Person to greet.")
        .withDefault("world")

    // Defining an option for a refined opaque type
    val nOpt =
      Opts.option[PositiveInt]("quiet", help = "Number of times message is printed.")
        .withDefault(PositiveInt(1))

    (userOpt, nOpt).mapN { (user, n) => 
      (1 to n).map(_ => println(s"Hello $user!"))
    }
  }
)
```
