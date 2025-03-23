---
title: "Refinement Methods"
---

# Refinement Methods

Iron provides multiple ways at compile time or runtime to refine a type depending on the use case.

## Automatic refinement

Unconstrained values are automatically cast to their refined form if they satisfy the constraint at compile time:

```scala 
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

val x: Int :| Greater[0] = 5
```

If they don't, a compile-time error is thrown:

```scala 
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

//}
val y: Int :| Greater[0] = -1
```

```scala 
-- Constraint Error --------------------------------------------------------
Could not satisfy a constraint for type scala.Int.

Value: -1
Message: Should be strictly greater than 0
----------------------------------------------------------------------------
```

If the value (or the constraint itself) cannot be evaluated at compile time, then the compilation also fails:

```scala 
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

//}
val runtimeValue: Int = ???
val x: Int :| Greater[0] = runtimeValue
```

```scala 
-- Constraint Error --------------------------------------------------------
Cannot refine non full inlined input at compile-time.
To test a constraint at runtime, use the `refine` extension method.

Note: Due to a Scala limitation, already-refined types cannot be tested at compile-time (unless proven by an `Implication`).

Inlined input: runtimeValue
----------------------------------------------------------------------------
```

Iron uses to determine if a value is evaluable at compile time the Scala
typeclass [FromExpr](https://scala-lang.org/api/3.2.0/scala/quoted/FromExpr.html).
By default, all fully inlined literals (including AnyVals, String, Option and Either) are evaluable at compile-time.
Note that the constraint condition also needs to be fully inlined.

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

//}
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
Fortunately, Iron supports explicit runtime checking using extension methods

### Imperative

You can imperatively refine a value at runtime (much like an assertion) using the `refine[C]` method:

```scala
val runtimeString: String = ???
val username: String :| Alphanumeric = runtimeString.refineUnsafe //or more explicitly, refineUnsafe[LowerCase].
```

The `refineUnsafe` extension method tests the constraint at runtime,
throwing an `IllegalArgumentException` if the value does not pass the assertion.

### Functional

Iron also provides methods similar to `refineUnsafe` but returning an `Option` (`refineOption`) or
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
createUser("Iltotore", 0)   //Left("Should be greater than 0")
createUser("Iltotore", 18)  //Right(User("Iltotore", 18))
```

### Accumulative error

You can accumulate refinement errors using the [Cats](../modules/cats.md) or [ZIO](../modules/zio.md) module.
Here is an example with the latter:

```scala
import zio.prelude.Validation

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.zio.*


type Username = DescribedAs[Alphanumeric, "Username should be alphanumeric"]

type Age = DescribedAs[Positive, "Age should be positive"]

case class User(name: String :| Username, age: Int :| Age)

def createUser(name: String, age: Int): Validation[String, User] =
  Validation.validateWith(
    name.refineValidation[Username],
    age.refineValidation[Age]
  )(User.apply)

createUser("Iltotore", 18)   //Success(Chunk(),User(Iltotore,18))
createUser("Il_totore", 18)  //Failure(Chunk(),NonEmptyChunk(Username should be alphanumeric))
createUser("Il_totore", -18) //Failure(Chunk(),NonEmptyChunk(Username should be alphanumeric, Age should be positive))
```

This is useful for forms where you want to report all input errors to the user and not short-circuit like an `Either`.

Check the [Cats module](../modules/cats.md) or [ZIO module](../modules/zio.md) page for further information.

### Refining further

Sometimes you want to refine the same value multiple times with different constraints.
This is especially useful when you want fine-grained refinement errors. Let's take the last example but with passwords:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type Username = DescribedAs[Alphanumeric, "Username should be alphanumeric"]
type Password = DescribedAs[
  Alphanumeric & MinLength[5] & Exists[Letter] & Exists[Digit],
  "Password should have at least 5 characters, be alphanumeric and contain at least one letter and one digit"
]

case class User(name: String :| Username, password: String :| Password)

def createUser(name: String, password: String): Either[String, User] =
  for
    validName     <- name.refineEither[Username]
    validPassword <- password.refineEither[Password]
  yield
    User(validName, validPassword)

createUser("Iltotore", "abc123") //Right(User("Iltotore", "abc123"))
createUser("Iltotore", "abc")    //Left("Password should have at least 5 characters, be alphanumeric and contain at least one letter and one digit")
```

At the last line, we get a `Left` saying that our password is invalid.
However, it's not clear which constraint is not satisfied: is my password to short? Should I add a digit? etc...

Using `refineFurther`/`refineFurtherEither`/... enables more detailed messages:

```scala
type Username = DescribedAs[Alphanumeric, "Username should be alphanumeric"]
type Password = DescribedAs[
  Alphanumeric & MinLength[5] & Exists[Letter] & Exists[Digit],
  "Password should have at least 5 characters, be alphanumeric and contain at least one letter and one digit"
]

case class User(name: String :| Username, password: String :| Password)

def createUser(name: String, password: String): Either[String, User] =
  for
    validName     <- name.refineEither[Username]
    alphanumeric  <- password.refineEither[Alphanumeric]
    minLength     <- alphanumeric.refineFurtherEither[MinLength[5]]
    hasLetter     <- minLength.refineFurtherEither[Exists[Letter]]
    validPassword <- hasLetter.refineFurtherEither[Exists[Digit]]
  yield
    User(validName, validPassword)

createUser("Iltotore", "abc123")   //Right(User("Iltotore", "abc123"))
createUser("Iltotore", "abc1")     //Left("Should have a minimum length of 5")
createUser("Iltotore", "abcde")    //Left("At least one element: (Should be a digit)")
createUser("Iltotore", "abc123  ") //Left("Should be alphanumeric")
```

Or with custom error messages:

```scala
type Username = DescribedAs[Alphanumeric, "Username should be alphanumeric"]
type Password = DescribedAs[
  Alphanumeric & MinLength[5] & Exists[Letter] & Exists[Digit],
  "Password should have at least 5 characters, be alphanumeric and contain at least one letter and one digit"
]

case class User(name: String :| Username, password: String :| Password)

def createUser(name: String, password: String): Either[String, User] =
  for
    validName     <- name.refineEither[Username]
    alphanumeric  <- password.refineEither[Alphanumeric].left.map(_ => "Your password should be alphanumeric")
    minLength     <- alphanumeric.refineFurtherEither[MinLength[5]].left.map(_ => "Your password should have a minimum length of 5")
    hasLetter     <- minLength.refineFurtherEither[Exists[Letter]].left.map(_ => "Your password should contain at least a letter")
    validPassword <- hasLetter.refineFurtherEither[Exists[Digit]].left.map(_ => "Your password should contain at least a digit")
  yield
    User(validName, validPassword)

createUser("Iltotore", "abc123")   //Right(User("Iltotore", "abc123"))
createUser("Iltotore", "abc1")     //Left("Your password should have a minimum length of 5")
createUser("Iltotore", "abcde")    //Left("Your password should contain at least a digit")
createUser("Iltotore", "abc123  ") //Left("Your password should be alphanumeric")
```

Note: Accumulative versions exist for [Cats](../modules/cats.md) and [ZIO](../modules/zio.md).

### Refining first order types

Iron provides utility methods to easily refine first order types (e.g container types like `List`, `Future`, `IO`...).

```scala
List(1, 2, 3).refineAllUnsafe[Positive]  //List(1, 2, 3): List[Int :| Positive]
List(1, 2, -3).refineAllUnsafe[Positive] //IllegalArgumentException
```

Variants exist for `Option/Either`, `assume`, `...Further` as well as `RefinedType` constructors.

## Assuming constraints

Sometimes, you know that your value always passes (possibly at runtime) a constraint. For example:

```scala
val random = scala.util.Random.nextInt(9)+1
val x: Int :| Positive = random
```

This code will not compile (see [Runtime refinement](#runtime-refinement)).
We could use `refineUnsafe` but we don't actually need to apply the constraint to `random` in this case.
Instead, we can can use `assume[C]`. It simply acts like a safer cast.

```scala
val random = scala.util.Random.nextInt(9)+1
val x: Int :| Positive = random.assume
```

This code will compile to:

```scala
val random: Int = scala.util.Random.nextInt(9)+1
val x: Int = random
```

leaving no overhead.