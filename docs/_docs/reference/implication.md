---
title: "Implication"
---

# Implication

Implication is a compile-time mechanism to allow casting from a refined type to another.
It is analogous to [logical implication](https://en.wikipedia.org/wiki/Material_conditional).

Implication is represented in Iron by `Implication[C1, C2]` or its alias `C1 ==> C2`.
It should be read as "C1 implies C2".

For example, the following code compiles due to [transitivity](https://en.wikipedia.org/wiki/Transitive_relation):

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Greater

//}
val x: Int :| Greater[5] = ???
val y: Int :| Greater[0] = x
```

Standard implications are usually stored in the same object as the associated constraint. For instance, the transitive
implication mentioned above is stored in [[numeric|io.github.iltotore.iron.constraint.numeric]].

## Creation

You can create your own constraint-to-constraint [[implications|io.github.iltotore.iron.package.Implication]] following this
single rule: a constraint `C1` can be cast to another `C2` if an implicit instance of `Implication[C1, C2]`
(or using the alias `C1 ==> C2`) is available.

Note: implications are a purely compile-time mechanism.

For example, the following implication:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Not

//}
given [C1]: (C1 ==> Not[Not[C1]]) = Implication()
```

allows us (if imported) to compile this code:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Not
import io.github.iltotore.iron.constraint.numeric.Greater

given [C1]: (C1 ==> Not[Not[C1]]) = Implication()

//}
val x: Int :| Greater[0] = ???
val y: Int :| Not[Not[Greater[0]]] = x //C1 implies Not[Not[C1]]: `x` can be safely casted.
```

### Dependencies

Almost every implication has a dependency. For example, our previous "double negation" implication doesn't work in the
following case:

```scala 
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Not
import io.github.iltotore.iron.constraint.numeric.Greater

//}
val x: Int :| Greater[1] = ???

//Assuming that Greater[1] ==> Greater[0]
val y: Int :| Not[Not[Greater[0]]] = x
```

But it should. This can be fixed by using two different constraints linked by an implication:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Not

//}
given [C1, C2](using C1 ==> C2): (C1 ==> Not[Not[C2]]) = Implication()
```

*Example taken from [[io.github.iltotore.iron.constraint.any]].*

Our implication now depends on another implication: `C1 ==> C2`. With this implementation, our code now compiles:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.any.Not
import io.github.iltotore.iron.constraint.numeric.Greater

given [C1, C2](using C1 ==> C2): (C1 ==> Not[Not[C2]]) = Implication()

//}
val x: Int :| Greater[1] = ???

//Assuming that Greater[1] ==> Greater[0]
val y: Int :| Not[Not[Greater[0]]] = x //(Greater[1] ==> Greater[0]) ==> (Greater[1] ==> Not[Not[Greater[0]]])
```

### Type operators

Scala 3 provides multiple compile-time constructs to help you to manipulate and test types.
The most notable ones are `scala.=:=` and [[io.github.iltotore.iron.ops]].

An instance of `A =:= B` is given by the compiler if `A` is the same type as `B`.
This can be combined with [[iron.ops|io.github.iltotore.iron.ops]] to create logical dependencies.

For example, we can implement the [transitive relation](https://en.wikipedia.org/wiki/Transitive_relation):

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.Greater

//}
import io.github.iltotore.iron.compileTime.*

given [V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()
```

*Example taken from [[numeric|io.github.iltotore.iron.constraint.numeric]].*

Now, the following code compiles:

```scala
//{
import io.github.iltotore.iron.*
import io.github.iltotore.iron.compileTime.*
import io.github.iltotore.iron.constraint.numeric.Greater

given [V1, V2](using V1 > V2 =:= true): (Greater[V1] ==> Greater[V2]) = Implication()

//}
val x: Int :| Greater[1] = ???
val y: Int :| Greater[0] = x //x > 1 and 1 > 0, so x > 0.
```
