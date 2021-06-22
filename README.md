# Iron

Iron is a type constraint system for Scala. It allows creating type-level assertions, evaluable at compile time and/or
runtime.

**Summary:**

- [Features](#Features)
- [Import in your project](#Import-in-your-project)
- [Contribute](#Contribute)

## Features

### Easy to use

Iron offers a simple way to create type-level assertions using Scala's givens and type aliases.
This syntactic sugar allows the user to create more readable constraints.

Example of a constraint alias:

```scala
type >[A, B] = A ==> Greater[B]

def log(x: Double > 0d): Double = Math.log(x)
log(-1d) //Compile time error
```

### Minimal overhead

When evaluated at compile time, almost all traces of type constraint disappear. They desugar to only one method returning
the passed argument:

```scala
inline def log(x: Double > 0d): Double = Math.log(x)
log(2d)
```

Desugars to:

```scala
Math.log(Constrained.unchecked(2d))
```

This will go to zero overhead
in [the next Dotty release](https://github.com/lampepfl/dotty/pull/12815).

### Consistency

Compile time assertions will fallback to runtime (disabled by default) only if they're not evaluable at compile time.

Iron relies heavily on Scala's inline feature instead of macros for compile time evaluation, leading to strong and
consistent rules:

- Fully inline constraints with inline input are guaranteed to be evaluated at compile time
- Non-fully inline constraints or inputs will be as optimized as possible by the language through the inline feature and
  will be evaluated at runtime.

### Configurability

The fallback behaviour can be configured using the `-Diron.fallback` argument to fit your needs:

- error (default): Throw an error if Iron is unable to evaluate the assertion at compile time
- warn: Warn and fallback to runtime evaluation if Iron is unable to evaluate the assertion at compile
- allow: Silently fallback to runtime evaluation if required

## Import in your project

<details>
<summary>
SBT
</summary>

```scala
libraryDependencies += "io.github.iltotore" %% "iron" % "version"
```

</details>

<details>
<summary>
Mill
</summary>

```scala
ivy"io.github.iltotore::iron:version"
```

</details>

Note: Replace `version` with the version of Iron

## Contribute

There is two main ways to contribute to Iron:

- Opening an issue for bugs/feature requests
- Forking then opening a pull request for changes