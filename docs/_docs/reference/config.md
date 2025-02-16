---
title: "Error messages configuration"
---

# Error messages configuration

Compile-time error messages can be tweaked either passing `-D...` properties to the JVM hosting the compiler (or BSP)
or using environment variables.

## Parameters

### Code format

Key: `-Diron.codeFormat` or `IRON_CODE_FORMAT`

Default: `short`

Change the format used to display the code in compile-time error messages.
Each format correspond to a [Printer](https://scala-lang.org/api/3.x/scala/quoted/Quotes$reflectModule$Printer.html).

| Value          | Printer                                                                                                              |
| -------------- | -------------------------------------------------------------------------------------------------------------------- |
| `full`         | [TreeCode](https://scala-lang.org/api/3.x/scala/quoted/Quotes$reflectModule$PrinterModule.html#TreeCode-0)           |
| `full_colored` | [TreeCodeAnsi](https://scala-lang.org/api/3.x/scala/quoted/Quotes$reflectModule$PrinterModule.html#TreeAnsiCode-0)   |
| `short`        | [TreeShortCode](https://scala-lang.org/api/3.x/scala/quoted/Quotes$reflectModule$PrinterModule.html#TreeShortCode-0) |
| `structure`    | [TreeStructure](https://scala-lang.org/api/3.x/scala/quoted/Quotes$reflectModule$PrinterModule.html#TreeStructure-0) |

### Short messages

Key: `-Diron.shortMessages` or `IRON_SHORT_MESSAGES`

Default: `false`

Use short one-line error messages or detailled ones. Short messages can be useful for IDE lenses (like Error Lens in VSCode).

Example:

```scala
val x: Int :| Positive = -5
```

<details>

<summary>true</summary>

```
Should be strictly positive: -5
```

</details>

<details>

<summary>false</summary>

```
-- Constraint Error --------------------------------------------------------
Could not satisfy a constraint for type scala.Int.

Value: -5
Message: Should be strictly positive
----------------------------------------------------------------------------
```

</details>

### Short reasons

Key: `-Diron.shortReasons` or `IRON_SHORT_REASONS`

Default: `true`

Use more concise reasons when a value cannot be refined at compile-time.
Full messages can be useful when debugging a custom constraint

<details>

<summary>true</summary>

```
-- Constraint Error --------------------------------------------------------
Cannot refine value at compile-time because the predicate cannot be evaluated.
This is likely because the condition or the input value isn't fully inlined.

To test a constraint at runtime, use one of the `refine...` extension methods.

Inlined input: a
Inlined condition: (((a.>(0.0): Boolean): Boolean).&&((a.<(100.0): Boolean)): Boolean)
Message: Should be strictly positive & Should be less than 100
Reason: 
- Term not inlined: a:
  - at main/src/io/github/iltotore/iron/constraint/any.scala:[2990..2995]
  - at main/src/io/github/iltotore/iron/macros/intersection.scala:[903..908]
----------------------------------------------------------------------------
```

</details>

<details>

<summary>false</summary>

```
-- Constraint Error --------------------------------------------------------
Cannot refine value at compile-time because the predicate cannot be evaluated.
This is likely because the condition or the input value isn't fully inlined.

To test a constraint at runtime, use one of the `refine...` extension methods.

Inlined input: a
Inlined condition: (((a.>(0.0): Boolean): Boolean).&&((a.<(100.0): Boolean)): Boolean)
Message: Should be strictly positive & Should be less than 100
Reason: Non-inlined boolean and. The following patterns are evaluable at compile-time:
- <inlined value> && <inlined value>
- <inlined value> && false
- false && <inlined value>

Left member:
  Some arguments of `>` are not inlined:
  Arg 0:
    Term not inlined: a

Right member:
  Some arguments of `<` are not inlined:
  Arg 0:
    Term not inlined: a
---------------------------------------------------------------------------
```

</details>

