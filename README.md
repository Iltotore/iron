![logo](https://github.com/iltotore/iron/blob/main/logo.png?raw=true)

[![iron Scala version support](https://index.scala-lang.org/iltotore/iron/iron/latest-by-scala-version.svg)](https://index.scala-lang.org/iltotore/iron/iron)
[![example workflow](https://github.com/Iltotore/iron/actions/workflows/main.yml/badge.svg)](https://github.com/Iltotore/iron/actions/workflows/main.yml)
___

Iron is a lightweight library for refined types in Scala 3.

It enables attaching constraints/assertions to types, to enforce properties and forbid invalid values. 

- **Prevent bugs.** In the spirit of static typing, Iron lets you use more specific types to prevent usage of wrong values.
- **Compile-time and run-time.** Evaluate constraints at compile time, or explicitly test values against them at runtime (e.g. for a form).
- **Seamless.** Iron types are subtypes of their unrefined versions, meaning you can easily add or remove them.
- **No black magic.** Use Scala 3's powerful inline, types and restricted macros for consistent behaviour and rules. No direct AST manipulation.
- **Extendable.** Easily create your own constraints or integrations using simple typeclasses.

To learn more about Iron, see the [microsite](https://iltotore.github.io/iron/docs/index.html).

## Example

```scala
def log(x: Double :| Greater[0.0]): Double =
  Math.log(x) //Used like a normal `Double`

log(1.0) //Automatically verified at compile time.
log(-1.0) //Error: -1.0 is not greater than 0.0!

val runtimeValue = ???
log(runtimeValue.refine) //Explicitly refine your external values at runtime.

runtimeValue.refineEither.map(log) //Use monadic style for functional validation
runtimeValue.refineEither[Greater[0.0]].map(log) //More explicitly
```

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

**Note: replace `version` with the version of Iron.**
