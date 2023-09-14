---
title: "ScalaCheck Support"
---

# ScalaCheck Support

This module provides `Arbitrary` instances for [ScalaCheck](https://scalacheck.org/).

## Dependency

SBT:

```scala 
libraryDependencies += "io.github.iltotore" %% "iron-scalacheck" % "version"
```

Mill:

```scala 
ivy"io.github.iltotore::iron-scalacheck:version"
```

## Arbitrary instances

All refined types have a fallback `Arbitrary` instance. Some constraints have a custom instance which is usually faster and prevents exhaustion.