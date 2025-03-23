---
title: "Creating New Types"
---

# Creating New Types

You can create no-overhead new types like [scala-newtype](https://github.com/estatico/scala-newtype) in Scala 2 with Iron.

## RefinedType

Iron provides a convenient trait called `RefinedType` to easily add smart constructors to your type:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]
```

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

//}
val temperature = Temperature(15) //Compiles
println(temperature) //15

val positive: Double :| Positive = 15
val tempFromIron = Temperature(positive) //Compiles too
```


### Runtime refinement

`RefinedType` supports [all refinement methods](refinement.md) provided by Iron:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

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

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

//}
val zioValidation: Validation[String, Temperature] = Temperature.validation(15)
```

Note: just like [IronType|io.github.iltotore.iron.IronType], [RefinedType|io.github.iltotore.iron.RefinedType]
compiles to its base type.

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

//}
val temperature: Temperature = Temperature(15)

val runtimeValue: Double = ???
val unsafeRuntime: Temperature = Temperature.applyUnsafe(runtimeValue)
```

compiles to

```scala
val temperature: Double = 15

val runtimeValue: Double = ???
val unsafeRuntime: Double = Temperature.applyUnsafe(runtimeValue)
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
type FirstName = FirstName.T
object FirstName extends RefinedType[String, Pure]
```
```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Pure

type FirstName = FirstName.T
object FirstName extends RefinedType[String, Pure]

//}
val firstName = FirstName("whatever")
```

## Opacity

A newtype is [opaque](https://docs.scala-lang.org/scala3/book/types-opaque-types.html).
Therefore, it is only known in its definition file. It is not considered to be a type alias outside of it:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]
```

```scala 
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

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
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

type Moisture = Moisture.T
object Moisture extends RefinedType[Double, Positive]
```

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

type Moisture = Moisture.T
object Moisture extends RefinedType[Double, Positive]

//}
case class Info(temperature: Temperature, moisture: Moisture)

val temperature: Temperature = ???
val moisture: Moisture = ???

Info(moisture, temperature) //Compile-time error
```

Therefore, it also forces the user to convert the value explicitly, for example using a smart constructor from
`RefinedType`:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]
```

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]

//}
val value: Double :| Positive = ???

val a: Temperature = value //Compile-time error
val b: Temperature = Temperature(value) //OK
```

### Inheriting base type

Assuming the following new type:

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

type FirstName = FirstNamle
object FirstName extends RefinedType[String, ForAll[Letter]]
```

## Typeclass derivation

Usually, transparent type aliases do not need a special handling for typeclass derivation as they can use the given
instances for `IronType`. However, this is not the case for opaque type aliases, for instance:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Positive

//}
type Temperature = Temperature.T
object Temperature extends RefinedType[Double, Positive]
```

To support such type, you can use the [[RefinedType.Mirror|io.github.iltotore.iron.RefinedType.Mirror]] provided by
each `RefinedType`. It works the same as
[Scala 3's Mirror](https://docs.scala-lang.org/scala3/reference/contextual/derivation.html#mirror). Here is an example
from the [ZIO JSON module](https://iltotore.github.io/iron/docs/modules/zio-json.html):

```scala
//{
import zio.json.*
import io.github.iltotore.iron.*

//}
inline given[T](using mirror: RefinedType.Mirror[T], ev: JsonDecoder[mirror.IronType]): JsonDecoder[T] =
  ev.asInstanceOf[JsonDecoder[T]]
```

In this example, given a new type `T` (like `Temperature` defined above), an implicit instance of `JsonEncoder`
for its underlying `IronType` (e.g `Double :| Positive`) is got and returned.

The types provided by [[RefinedType.Mirror|io.github.iltotore.iron.RefinedType.Mirror]] are:
- `BaseType`: the base (unrefined) type of the mirrored type.
- `ConstraintType`: the constraint type of the mirrored new type.
- `IronType`: an alias for `BaseType :| ConstraintType`
- `FinalType`: the underlying type of the mirrored new type. Equivalent to its `IronType` if the alias is not opaque.
