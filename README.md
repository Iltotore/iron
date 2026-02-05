![logo](https://github.com/Iltotore/iron/blob/main/logo.png?raw=true)

[![Scala version support](https://index.scala-lang.org/iltotore/iron/iron/latest-by-scala-version.svg)](https://index.scala-lang.org/iltotore/iron/iron)
[![CI](https://github.com/Iltotore/iron/actions/workflows/ci.yml/badge.svg)](https://github.com/Iltotore/iron/actions/workflows/ci.yml)
___

Iron is a lightweight library for refined types in Scala 3.

It enables attaching constraints/assertions to types, to enforce properties and forbid invalid values.

- **Catch bugs.** In the spirit of static typing, use more specific types to avoid invalid values.
- **Compile-time and runtime.** Evaluate constraints at compile time, or explicitly check them at runtime (e.g. for a
  form).
- **Seamless.** Iron types are subtypes of their unrefined versions, meaning you can easily add or remove them.
- **No black magic.** Use Scala 3's powerful inline, types and restricted macros for consistent behaviour and rules. No
  unexpected behaviour.
- **Extendable.** Easily create your own constraints or integrations using classic typeclasses.

To learn more about Iron, see the [microsite](https://iltotore.github.io/iron/docs/index.html).

## Example

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

def log(x: Double :| Positive): Double =
  Math.log(x) //Used like a normal `Double`

log(1.0) //Automatically verified at compile time.
log(-1.0) //Compile-time error: Should be strictly positive

val runtimeValue: Double = ???
log(runtimeValue.refineUnsafe) //Explicitly refine your external values at runtime.

runtimeValue.refineEither.map(log) //Use monadic style for functional validation
runtimeValue.refineEither[Positive].map(log) //More explicitly
```

## Helpful error messages

Iron provides useful errors when a constraint does not pass:

```scala
log(-1.0)
```

```scala
-- Constraint Error --------------------------------------------------------
Could not satisfy a constraint for type scala.Double.

Value: -1.0
Message: Should be strictly positive
----------------------------------------------------------------------------
```

Or when it cannot be verified:

```scala
val runtimeValue: Double = ???
log(runtimeValue)
```

```scala
-- Constraint Error --------------------------------------------------------
Cannot refine non full inlined input at compile-time.
To test a constraint at runtime, use the `refine` extension method.

Note: Due to a Scala limitation, already-refined types cannot be tested at compile-time (unless proven by an `Implication`).

Inlined input: runtimeValue
----------------------------------------------------------------------------
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

| Module          | JVM | JS | Native |
|-----------------|-----|----|--------|
| iron            | ✔️  | ✔️ | ✔️     |
| iron-borer      | ✔️  | ✔️ | ❌     |
| iron-cats       | ✔️  | ✔️ | ✔️     |
| iron-circe      | ✔️  | ✔️ | ✔️     |
| iron-ciris      | ✔️  | ✔️ | ✔️     |
| iron-chimney    | ✔️  | ✔️ | ✔️     |
| iron-decline    | ✔️  | ✔️ | ✔️     |
| iron-doobie     | ✔️  | ❌  | ❌    |
| iron-dynosaur   | ✔️  | ✔️  | ❌    |
| iron-jsoniter   | ✔️  | ✔️ | ✔️     |
| iron-pureconfig | ✔️  | ❌️ | ❌️     |
| iron-scalacheck | ✔️  | ✔️ | ❌     |
| iron-scodec     | ✔️  | ✔️ | ✔️     |
| iron-skunk      | ✔️  | ✔️ | ✔️     |
| iron-upickle    | ✔️  | ✔️ | ✔️     |
| iron-zio        | ✔️  | ✔️ | ❌     |
| iron-zio-json   | ✔️  | ✔️ | ❌     |
| iron-play-json  | ✔️  | ✔️ | ❌     |

## Adopters

Here is a non-exhaustive list of projects using Iron.

[Submit a PR](https://github.com/Iltotore/iron/pulls?q=is%3Apr+is%3Aopen+sort%3Aupdated-desc) to add your project or
company to the list.

### Companies

- [AshFall Studio](https://ashfallstudio.com)
- [Association familiale Mulliez](https://fr.wikipedia.org/wiki/Association_familiale_Mulliez)
- [Clever Cloud](https://www.clever-cloud.com)
- [Ledger](https://github.com/LedgerHQ)
- [Marss](https://github.com/marss)
- [NuMind](https://numind.ai)
- [Sergic](https://www.sergic.com)

### Other projects

- [gvolpe/trading](https://github.com/gvolpe/trading) ([book](https://leanpub.com/feda))
- [cheleb/laminar-form-derivation](https://github.com/cheleb/laminar-form-derivation)
- [Lichess](https://lichess.org)
- [Tessella](https://github.com/scala-tessella/tessella)

## Useful links

- [Website](https://iltotore.github.io/iron/docs/index.html)
- [Scaladoc](https://iltotore.github.io/iron/index.html)
- [Code of Conduct](https://iltotore.github.io/iron/docs/code-of-conduct.html)
- [Contributing to Iron](https://iltotore.github.io/iron/docs/contributing.html)
