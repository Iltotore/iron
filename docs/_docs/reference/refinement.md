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
