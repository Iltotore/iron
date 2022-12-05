---
title: "Refinement Methods"
---

# Refinement Methods

Iron provides multiple ways at compile time or runtime to refine a type depending on the use case.

## Automatic refinement

Unconstrained values are automatically cast to their refined form if they satisfy the constraint at compile time:

```scala
//This import will be assumed in next examples.

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

val x: Int :| Greater[0] = 5
```

If they don't, a compile-time error is thrown:

```scala
val y: Int :| Greater[0] = -1
```

> Error: Should be greater than 0

If the value (or the constraint itself) cannot be evaluated at compile time, then the compilation also fails:

```scala
val runtimeValue: Int = ???
val x: Int :| Greater[0] = runtimeValue
```

> Cannot refine non fully inlined input at compile-time.
> To test a constraint at runtime, use the `refined` extension method.
>
> Inlined input: runtimeValue

Iron uses to determine if a value is evaluable at compile time the Scala
typeclass [FromExpr](https://scala-lang.org/api/3.2.0/scala/quoted/FromExpr.html).
By default, all fully inlined literals (including AnyVals, String, Option and Either) are evaluable at compile-time.
Note that the constraint condition also needs to be fully inlined.

```scala
inline val value = 2
val x: Int :| Greater[0] = value //OK
```

## Runtime refinement

Sometimes, you want to refine a value that is not available at compile time. For example in the case of form validation.

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.string.*

val runtimeString: String = ???
val username: String :| Alphanumeric = runtimeString
```

This snippet would not compile because `runtimeString` is not evaluable at compile time.
Fortunately, Iron supports explicit runtime checking through `refine`:

```scala
val runtimeString: String = ???
val username: String :| Alphanumeric = runtimeString.refine //or more explicitly, refine[LowerCase].
```

The `refine` extension method tests the constraint at runtime, throwing an `IllegalArgumentException` if the value
didn't pass
the assertion.

Iron also provides methods similar to `refine` but returning an `Option` (`refineOption`) or
an `Either` (`refineEither`), useful for data validation:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

case class User(name: String :| Alphanumeric, age: Int :| Greater[0])

def createUser(name: String, age: Int): Either[String, User] =
  for
    n <- name.refineEither[Alphanumeric]
    a <- age.refineEither[Greater[0]]
  yield User(n, a)

createUser("Il_totore", 18) //Left("Should be alphanumeric")
createUser("Iltotore", 0) //Left("Should be greater than 0")
createUser("Iltotore", 18) //Right(User("Iltotore", 18))
```
