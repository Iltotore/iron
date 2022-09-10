![logo](https://github.com/iltotore/iron/blob/main/logo.png?raw=true)

[![iron Scala version support](https://index.scala-lang.org/iltotore/iron/iron/latest-by-scala-version.svg)](https://index.scala-lang.org/iltotore/iron/iron)
[![example workflow](https://github.com/Iltotore/iron/actions/workflows/main.yml/badge.svg)](https://github.com/Iltotore/iron/actions/workflows/main.yml)
___

Iron is a lightweight library for refined types in Scala 3.

It allows to attach constraints/assertions to types to enforce some properties and forbid invalid values. 

- **Prevent bugs.** In the continuity of static typing, Iron lets you use more specific types to prevent usage of wrong values.
- **Compile-time and run-time.** Evaluate constraints at compile time or explicitly test them at run time for external values (e.g a form).
- **Seamless.** Iron types are subtypes of their unrefined version meaning you can easily add it and remove it from your project.
- **No black magic.** Use Scala 3's powerful inline, types and restricted macros for consistent behaviour and rules. No direct AST manipulation.
- **Extendable.** Easily create your own constraints or integrations using simple typeclasses.

## Example

```scala
def log(x: Double :| Greater[0.0]): Double =
  Math.log(x) //Used like a normal `Double`
  
  
log(1.0) //Automatically verified at compile time.
log(-1.0) //Error: -1.0 is not greater than 0.0!

val runtimeValue = ???
log(runtimeValue.refine) //Explicitly refine at run time your external values.

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