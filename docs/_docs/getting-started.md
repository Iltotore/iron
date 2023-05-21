---
title: "Getting Started"
---

# Getting Started

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
import io.github.iltotore.iron.{given, *}
```

This import contains bases to make Iron work including:

- Implicit conversion from raw type to its refined form (aka auto refinement)
- Utility methods `refine`, `refineEither` and `refineOption`
- Common constraints from package `io.github.iltotore.iron.constraint.any`

### Constraint imports

Standard constraints are split in different objects stored in the package `io.github.iltotore.iron.constraint`.

For example, you can import standard number-related constraints using:

```scala
import io.github.iltotore.iron.constraint.numeric.*
```

When having multiple imports from Iron, this style is often preferred in Iron codebase or documentation:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
```

### Companion object
`RefinedTypeOps` create convenient companion object.

```scala
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]
```

#### apply function for compile-time validation
```scala
val temperature = Temperature(100) //temperature is of Temperature type
```
`Temperature(-100)` won't compile.

#### fromIronType to wrap already refined values

`fromIronType` helps to manage values which already were checked against constrains.

Implication works here; hence, both `Double :| Greater[10]` and `Double :| Positive` could be `Temperature`
```scala
val x: Double :| Positive = 5.0
val y: Double :| Greater[10] = 15.0
val t1 = Temperature.fromIronType(x)
val t2 = Temperature.fromIronType(y)
```

#### Option/Either
`Temperature.either(-5.0)` and `Temperature.option(-5.0)` return `Either` and `Option`.

#### applyUnsafe
`applyUnsafe` throws `IllegalArgumentException` exception if predicate fails.
`Temperature.applyUnsafe(-1)` is example of usage.

#### assume
`Temperature.assume(x)` doesn't fail (and returns `Temperature`). No validations being performed. Consider it as unsafe cast.

## Next steps

You can find the list of all standard constraints in the [[constraint package summary|io.github.iltotore.iron.constraint]].

Now that you know how to import Iron in your project, you should check the [references](reference/index.md):
- [Iron Type](reference/iron-type.md): the core data type of Iron.
- [Constraint](reference/constraint.md): to create your own constraints.
- [Refinement Methods](reference/refinement.md): to use constraints and refine values.
