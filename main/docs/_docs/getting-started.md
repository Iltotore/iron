---
title: "Getting started"
---

## Dependency

Include Iron in your project using your preferred build tool.

SBT: 

```scala
libraryDependencies += "io.github.iltotore" %% "iron" % "version"
```

Mill:

```scala
ivy"io.github.iltotore::iron:version"
```

## Common imports

### Base

The following import is often used:

```scala
import io.github.iltotore.iron.*
```

This import contains bases to make Iron work including:

- Implicit conversion from raw type to its refined form (aka auto refinement)
- Utility methods `refine`, `refineEither` and `refineOption`
- Common constraints from package `io.github.iltotore.iron.constraint.any`

### Constraint imports

Standard constraints are split in different objects stored in the package `io.github.iltotore.iron.constraint`.

For example, you can import standard number-related constraints using:

```scala
import io.github.iltotore.iron.constraint.numeric.{*, given}
```

**Note: Don't forget the `given` import. It imports `Constraint` implicit instances. See [Importing Given](http://dotty.epfl.ch/docs/reference/contextual/given-imports.html).**

You can find the list of all standard constraints in the [[constraint package summary|io.github.iltotore.iron.constraint]]

