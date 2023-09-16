---
title: Comparison to refined
---

# Comparison to refined

[Refined](https://github.com/Iltotore/iron) is another refined types library for Scala.

## Scala versions support

Unlike Iron, Refined supports Scala 2. However, its Scala 3 support is incomplete:
- Some methods available in the Scala 2 artifact are missing
- Only runtime refinement is supported

Iron fully supports Scala 3, including both runtime and compile-time refinements. It also leverages some
Scala 3-exclusive features. See below.

## Internals

Iron and Refined took different paths for their internal infrastructures. Refined uses
[Shapeless](https://github.com/milessabin/shapeless), a library for generic programming in Scala. It was mandatory in
Scala 2 because the language didn't have the required features to build type refinements in a painless way.

Iron's core does not use any external library for generic programming and compile-time internals. Scala 3 already
has the mandatory features and removes the need for shapeless. Iron leverages these features, especially:
- [Opaque types](https://docs.scala-lang.org/scala3/book/types-opaque-types.html)
- [Type classes improvements](https://docs.scala-lang.org/scala3/book/ca-type-classes.html)
- [Inline](https://docs.scala-lang.org/scala3/guides/macros/inline.html)
- [New macro API](https://docs.scala-lang.org/scala3/guides/macros/macros.html)
- [`scala.compiletime` package](https://dotty.epfl.ch/api/scala/compiletime.html)

Resulting in several pros:
- Less magic
- Shorter compilation times
- Lighter artifact
- Better UX

## Ecosystem

Refined and Iron have equally rich standard libraries, with many constraints out of the box. However, Refined has
a more mature ecosystem overall, with more external modules and supported libraries.

Both libraries are easy to make compatible with other projects using type classes and ad-hoc polymorphism. Iron has
an additional benefit: since a refined type `A :| C` is a subtype of its base type `A`, it interacts well with variance.

For example, most of [ZIO Prelude's functional abstractions](https://zio.dev/zio-prelude/functional-abstractions/) do
not need to be implemented for Iron, since variance already does it for us.
