![logo](/logo.png?raw=true)

[![Scala version support](https://index.scala-lang.org/iltotore/iron/iron/latest-by-scala-version.svg)](https://index.scala-lang.org/iltotore/iron/iron)
[![CI](https://github.com/Iltotore/iron/actions/workflows/ci.yml/badge.svg)](https://github.com/Iltotore/iron/actions/workflows/ci.yml)

⚠️ **Iron v2.0.0 is still in development. Please check [old/1.x](https://github.com/Iltotore/iron/tree/old/1.x).**
___

Iron is a lightweight library for refined types in Scala 3.

It enables attaching constraints/assertions to types, to enforce properties and forbid invalid values. 

- **Catch bugs.** In the spirit of static typing, use more specific types to avoid invalid values.
- **Compile-time and runtime.** Evaluate constraints at compile time, or explicitly check them at runtime (e.g. for a form).
- **Seamless.** Iron types are subtypes of their unrefined versions, meaning you can easily add or remove them.
- **No black magic.** Use Scala 3's powerful inline, types and restricted macros for consistent behaviour and rules. No unexpected behaviour.
- **Extendable.** Easily create your own constraints or integrations using classic typeclasses.

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

SBT:

```scala
libraryDependencies += "io.github.iltotore" %% "iron" % "version"
```


Mill:

```scala
ivy"io.github.iltotore::iron:version"
```

**Note: replace `version` with the version of Iron.**

### Platform support

| Module        | JVM | JS  | Native |
|---------------|-----|-----|--------|
| iron          | ✔️  | ✔️  | ✔️     |
| iron-cats     | ✔️  | ✔️  | ✔️     |
| iron-circe    | ✔️  | ✔️  | ✔️     |
| iron-zio      | ✔️  | ✔️  | ❌      |
| iron-zio-json | ✔️  | ✔️  | ❌      |

## Useful links

- [Website](https://iltotore.github.io/iron/docs/index.html)
- [Scaladoc](https://iltotore.github.io/iron/index.html)
- [Code of Conduct](https://iltotore.github.io/iron/docs/code-of-conduct.html)
- [Contributing to Iron](https://iltotore.github.io/iron/docs/contributing.html)