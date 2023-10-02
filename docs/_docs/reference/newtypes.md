---
title: "Creating New Types"
---

# Creating New Types

You can create no-overhead new types like [scala-newtype](https://github.com/estatico/scala-newtype) in Scala 2 with Iron.

## RefinedTypeOps

Iron provides a convenient trait called `RefinedTypeOps` to easily add smart constructors to your type:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]
```

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]

//}
val temperature = Temperature(15) //Compiles
println(temperature) //15

val positive: Double :| Positive = 15
val tempFromIron = Temperature(positive) //Compiles too
```

For transparent type aliases, it is possible to use the `RefinedTypeOps.Transparent` alias to avoid boilerplate.

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps.Transparent[Temperature]
```

### Runtime refinement

`RefinedTypeOps` supports [all refinement methods](refinement.md) provided by Iron:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps.Transparent[Temperature]

//}
val unsafeRuntime: Temperature = Temperature.applyUnsafe(15)
val option: Option[Temperature] = Temperature.option(15)
val either: Either[String, Temperature] = Temperature.either(15)
```

Constructors for other modules exist:

```scala
//{
import zio.prelude.Validation

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.zio.*

type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps.Transparent[Temperature]

//}
val zioValidation: Validation[String, Temperature] = Temperature.validation(15)
```

Note: all these constructors are inline. They don't bring any overhead:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Temperature]

//}
val temperature: Temperature = Temperature(15)

val runtimeValue: Double = ???
val unsafeRuntime: Temperature = Temperature.applyUnsafe(runtimeValue)
```

compiles to

```scala
val temperature: Double = 15

val runtimeValue: Double = ???
val unsafeRuntime: Double =
  if runtimeValue > 0 then runtimeValue
  else throw new IllegalArgumentException("...")
```

## New type with no constraint

You can create a new type without restriction by using the [[Pure|io.github.iltotore.iron.constraint.any.Pure]]
constraint. [[Pure|io.github.iltotore.iron.constraint.any.Pure]] is an alias for
[[True|io.github.iltotore.iron.constraint.any.True]], a constraint that is always satisfied.

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Pure

//}
type FirstName = String :| Pure
object FirstName extends RefinedTypeOps[FirstName]
```
```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Pure

type FirstName = String :| Pure
object FirstName extends RefinedTypeOps[FirstName]

//}
val firstName = FirstName("whatever")
```

## Opaque new types

The aliased type of an [opaque type](https://docs.scala-lang.org/scala3/book/types-opaque-types.html) is only known in its definition file. It is not considered like a type alias outside of it:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]
```

```scala 
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]

//}
val x: Double :| Positive = 5
val temperature: Temperature = x //Error: Temperature expected, got Double :| Positive
```

Such encapsulation is especially useful to avoid mixing different domain types with the same refinement:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]

opaque type Moisture = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Moisture]
```

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]

opaque type Moisture = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Moisture]

//}
case class Info(temperature: Temperature, moisture: Moisture)

val temperature: Temperature = ???
val moisture: Moisture = ???

Info(moisture, temperature) //Compile-time error
```

Therefore, it also forces the user to convert the value explicitly, for example using a smart constructor from
`RefinedTypeOps`:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]
```

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]

//}
val value: Double :| Positive = ???

val a: Temperature = value //Compile-time error
val b: Temperature = Temperature(value) //OK
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
object FirstName extends RefinedTypeOps[String, ForAll[Letter], FirstName]
```

We cannot use `java.lang.String`'s methods neither pass `FirstName` as a String without using the `value`
extension method. In Scala 3, opaque types can be a subtype of their underlying type:

```scala
opaque type Foo <: String = String
object Foo:
  def apply(value: String): Foo = value
```
```scala
//{
opaque type Foo <: String = String
object Foo:
  def apply(value: String): Foo = value

//}
val x = Foo("abcd")
x.toUpperCase //"ABCD"
```

Therefore, you can combine it with `RefinedTypeOps`:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

//}
opaque type FirstName <: String :| ForAll[Letter] = String :| ForAll[Letter]
object FirstName extends RefinedTypeOps[String, ForAll[Letter], FirstName]
```

```scala 
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

opaque type FirstName <: String :| ForAll[Letter] = String :| ForAll[Letter]
object FirstName extends RefinedTypeOps[String, ForAll[Letter], FirstName]

//}
val x = FirstName("Raphael")
x.toUpperCase //"RAPHAEL"
```

## Typeclass derivation

Usually, transparent type aliases do not need a special handling for typeclass derivation as they can use the given
instances for `IronType`. However, this is not the case for opaque type aliases, for instance:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
opaque type Temperature = Double :| Positive
object Temperature extends RefinedTypeOps[Double, Positive, Temperature]
```

To support such type, you can use the [[RefinedTypeOps.Mirror|io.github.iltotore.iron.RefinedTypeOps.Mirror]] provided by
each `RefinedTypeOps`. It works the same as
[Scala 3's Mirror](https://docs.scala-lang.org/scala3/reference/contextual/derivation.html#mirror). Here is an example
from the [ZIO JSON module](https://iltotore.github.io/iron/docs/modules/zio-json.html):

```scala
//{
import zio.json.*
import io.github.iltotore.iron.*

//}
inline given[T](using mirror: RefinedTypeOps.Mirror[T], ev: JsonDecoder[mirror.IronType]): JsonDecoder[T] =
  ev.asInstanceOf[JsonDecoder[T]]
```

In this example, given a new type `T` (like `Temperature` defined above), an implicit instance of `JsonEncoder` for its
underlying `IronType` (e.g `Double :| Positive`) is got and returned.

The types provided by [[RefinedTypeOps.Mirror|io.github.iltotore.iron.RefinedTypeOps.Mirror]] are:
- `BaseType`: the base (unrefined) type of the mirrored type.
- `ConstraintType`: the constraint type of the mirrored new type.
- `IronType`: an alias for `BaseType :| ConstraintType`
- `FinalType`: the underlying type of the mirrored new type. Equivalent to its `IronType` if the alias is not opaque.