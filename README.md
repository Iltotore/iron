![logo](https://github.com/iltotore/iron/blob/main/logo.png?raw=true)

[![iron Scala version support](https://index.scala-lang.org/iltotore/iron/iron/latest-by-scala-version.svg)](https://index.scala-lang.org/iltotore/iron/iron)
[![example workflow](https://github.com/Iltotore/iron/actions/workflows/main.yml/badge.svg)](https://github.com/Iltotore/iron/actions/workflows/main.yml)

Iron is a type constraint system for Scala. It allows creating type-level assertions, evaluable at compile time and/or
runtime.

**Summary:**

- [Features](#Features)
- [Import in your project](#Import-in-your-project)
- [Modules](#Modules)
- [Useful links](#Useful-links)
- [Contribute](#Contribute)

## Features

### Easy to use

Iron offers a simple way to create type-level assertions using Scala's givens and type aliases.
This syntactic sugar allows the user to create more readable constraints.

Example of a constraint alias:

```scala
//This is mainly implicit functions/instances but if you prefer without star import, you can specify them.
import iron.*, constraint.*, numeric.constraint.{given, Greater}

type >[A, B] = A ==> Greater[B]

def log(x: Double > 0d): Refined[Double] = x.map(Math.log)
log(-1d) //Compile time error
```

Refined parameters return an Either to allow
[functional error handling](https://docs.scala-lang.org/overviews/scala-book/functional-error-handling.html) when using
runtime constraints:
```scala
def log(x: Double > 0d): Refined[Double] = x.map(Math.log)

val runtime = 1d
log(runtime) //Either[IllegalValueError[Double], Double] (Refined[Double])
```

### Minimal overhead

When evaluated at compile time, almost all traces of type constraint disappear. They desugar directly to a Right
without unneeded assertion:

```scala
inline def log(x: Double > 0d): Refined[Double] = x.map(Math.log)
log(2d)
```

Desugars to:

```scala
Right(Constrained.apply(2d)).map(Math.log)
```

Note: Once compiled, `Constrained.apply` returns the passed argument. This dummy method will be removed once
[the next Dotty release](https://github.com/lampepfl/dotty/pull/12815).

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

This behaviour can be configured individually using `Constraint.RuntimeOnly[A, B]` or `Constraint.CompileTimeOnly[A, B]`.

## Import in your project

<details>
<summary>SBT</summary>

```scala
libraryDependencies += "io.github.iltotore" %% "iron" % "version"
```

</details>

<details>
<summary>Mill</summary>

```scala
ivy"io.github.iltotore::iron:version"
```

</details>

Note: Replace `version` with the version of Iron

## Modules
Iron modules are versioned using the schema `ironMajorVersion-moduleVersion`.

Official modules:
- [Iterable](https://github.com/Iltotore/iron/tree/main/iterable)
- [Numeric](https://github.com/Iltotore/iron/tree/main/numeric)
- [String](https://github.com/Iltotore/iron/tree/main/string)

## Useful links
- [Wiki](https://github.com/Iltotore/iron/wiki)
- [Scaladoc](https://iltotore.github.io/iron/scaladoc)
- [Ask a question (Issues section)](https://github.com/Iltotore/iron/issues)

## Contribute

There is two main ways to contribute to Iron:

- Opening an issue for bugs/feature requests
- Forking then opening a pull request for changes