---
title: "Overview"
---

Iron is a type constraints (or "refined types") library for Scala. It allows to bind constraints to a specific type.
This processus is called "type refinement".

## Why refined types matter

In production code bases, it is important to make sure that all values passed are valid or handled correctly.
Static typing like in Scala is useful to avoid using the wrong datatype.
However, despite strong static typing, some wrong values can bypass type checking.

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
- The assertion is only evaluated at run time: bugs/wrong values may not be detected

Refined types solve both problems by ensuring that constraints are checked compile time or __explicitly__ at run time.

## Use cases

Iron and refined types in general are useful in multiple cases:
- Mathematics
- Data validation (form, API...)
- Securing business data types

## Getting started

See the [Getting started](getting-started.md) page to set up and start using Iron.