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
import io.github.iltotore.iron.constraint.numeric.*
```

When having multiple imports from Iron, this style is often preferred in Iron codebase or documentation:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
```

## Next steps

You can find the list of all standard constraints in the [[constraint package summary|io.github.iltotore.iron.constraint]].

Now that you know how to import Iron in your project, you should check the [references](reference/index.md):
- [Iron Type](reference/iron-type.md): the core data type of Iron.
- [Constraint](reference/constraint.md): to create your own constraints.
- [Refinement Methods](reference/refinement.md): to use constraints and refine values.
- [New types](reference/newtypes.md): to encapsulate business refined types.
