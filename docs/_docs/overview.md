---
title: "Overview"
---

# Overview

Iron is a type constraints (or "refined types") library for Scala. It enables binding constraints to a specific type.
This processus is called "type refinement".

## Why refined types matter

In production code bases, it is important to make sure that all values passed are valid or handled correctly.
Static typing like in Scala is useful to avoid using such issue.
However, despite strong static typing, some invalid values still type check.

Taking for example a User with an age:

```scala
case class User(age: Int)
```

Thanks to static typing, we cannot pass values of a different type:

```scala
User(1) //OK
User("1") //Error
```

However, unexpected values can still be passed because `Int` contains "wrong" values for our use case:

```scala
User(-1) //A User with a negative age?
```

To fix this caveat, you have to write an assertion/guard condition with the following drawbacks:
- You have make sure your value is verified wherever you use it
- The assertion is only evaluated at runtime: bugs/wrong values may not be detected

Refined types solve both problems by ensuring that constraints are checked compile time or __explicitly__ at runtime.

```scala
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.numeric.*

case class User(age: Int :| Greater[0])

User(1) //Compiles
User("1") //Does not compile
User(-1) //Does not compile
User(-1.refine) //Compiles but fails at runtime. Useful for runtime checks such as form validation.
//See also `refineOption` and `refineEither`
```

## Use cases

Iron and refined types in general are useful in many cases:
- Data validation (form, API...)
- Securing business data types
- Mathematics

## Getting started

See the [Getting started](getting-started.md) page to set up and start using Iron.

See [references](reference/index.md) for details about the concepts of Iron.
