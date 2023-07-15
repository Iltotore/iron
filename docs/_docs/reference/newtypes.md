---
title: "Creating New Types"
---

# Creating New Types

You can create no-overhead new types like [scala-newtype](https://github.com/estatico/scala-newtype) in Scala 2 with Iron.

## RefinedTypeOps

Iron provides a convenient trait called `RefinedTypeOps` to easily add smart constructors to your type:

```scala
type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]
```

```scala
val temperature = Temperature(15) //Compiles
println(temperature) //15

val positive: Int :| Positive = 15
val tempFromIron = Temperature(positive) //Compiles too
```

### Runtime refinement

`RefinedTypeOps` supports [all refinement methods](refinement.md) provided by Iron:

```scala
val unsafeRuntime: Temperature = Temperature.applyUnsafe(15)
val option: Option[Temperature] = Temperature.option(15)
val either: Either[String, Temperature] = Temperature.either(15)
```

Constructors for other modules exist:

```scala
val zioValidation: Temperature = Temperature.validation(15)
```

Note: all these constructors are inline. They don't bring any overhead:

```scala
val temperature: Temperature = Temperature(15)
val unsafeRuntime: Temperature = Temperature.applyUnsafe(runtimeValue)
```

compiles to

```scala
val temperature: Double = 15
val unsafeRuntime: Double =
  if runtimeValue > 0 then runtimeValue
  else throw new IllegalArgumentException("...")
```

## New type with no constraint

You can create a new type without restriction by using the [[Pure|io.github.iltotore.iron.constraint.any.Pure]]
constraint. [[Pure|io.github.iltotore.iron.constraint.any.Pure]] is an alias for
[[True|io.github.iltotore.iron.constraint.any.True]], a constraint that is always satisfied.

```scala
type FirstName = String :| Pure
object FirstName extends RefinedTypeOps[FirstName]
```
```scala
val firstName = FirstName("whatever")
```

## Opaque new types

The aliased type of an [opaque type](https://docs.scala-lang.org/scala3/book/types-opaque-types.html) is only known in its definition file. It is not considered like a type alias outside of it:

```scala
opaque type Temperature = Double :| Positive
```

```scala
val x: Double :| Positive = 5
val temperature: Temperature = x //Error: Temperature expected, got Double :| Positive
```

Such encapsulation is especially useful to avoid mixing different domain types with the same refinement:

```scala
opaque type Temperature = Double :| Positive
opaque type Moisture = Double :| Positive
```

```scala
case class Info(temperature: Temperature, moisture: Moisture)

val temperature: Temperature = ???
val moisture: Moisture = ???

Info(moisture, temperature) //Compile-time error
```

But as is, you cannot create an "instance" of your opaque type outside of its definition file. You need to add methods yourself like:

```scala
opaque type Temperature = Double :| Positive

object Temperature:

  def apply(x: Double :| Positive): Temperature = x
```

Note: due to a [compiler bug](https://github.com/lampepfl/dotty/issues/17984), incremental/cross-module compilation can fail.
[An easy workaround](https://github.com/Iltotore/iron/issues/131#issuecomment-1614974318) but with more boilerplate is
to use `RefinedTypeOpsImpl[A, C, T]` where:
- `A` is the base type
- `C` is the constraint type
- `T` is the type alias

### Inheriting base type

Assuming the following new type:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

opaque type FirstName = String :| ForAll[Letter]
object FirstName extends RefinedTypeOps[FirstName]
```

We cannot use `java.lang.String`'s methods neither pass `FirstName` as a String without using the `value`
extension method. In Scala 3, opaque types can be a subtype of their underlying type:

```scala
opaque type Foo <: String = String
object Foo:
  def apply(value: String): Foo = value
```
```scala
val x = Foo("abcd")
x.toUpperCase //"ABCD"
```

Therefore, you can combine it with `RefinedTypeOps`:

```scala
opaque type FirstName <: String :| ForAll[Letter] = String :| ForAll[Letter]
object FirstName extends RefinedTypeOps[FirstName]
```
```scala
val x = FirstName("Raphael")
x.toUpperCase //"RAPHAEL"
```