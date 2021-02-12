# ScalaLint
*Compiled Once, Reliable Everywhere.*

ScalaLint is an advanced type constraints system.
It allows you to add assertion/conditions to types of parameters, variables or method returned value.

# Features
| type                    | location                                       | state           |
| ----------------------- | ---------------------------------------------- | --------------- |
| runtime constraint      | parameters, variables, method return-type      | ✅              |
| inlined constraint      | parameters, variables, method return-type      | ✅              |
| compile-time constraint | parameters, variables, method return-type      | experimental    |
| constraint mixin        | any constraint type                            | ✅              |

# Install
ScalaLint is pullable from Maven Central.

<details>
<summary>Using SBT</summary>

```scala
libraryDependencies += "io.github.iltotore" %% "scalalint" % "<version>"
```
</details>

<details>
<summary>Using Mill</summary>

```scala
ivy"io.github.iltotore::scalalint:<version>"
```
</details>

# Usage
## Creating a runtime constraint

You firstly need to create a trait extending ConstraintAnchor

```scala
import io.github.iltotore.scalalint.constraint.ConstraintAnchor

trait Positive extends ConstraintAnchor
```

You now need to create an implicit instance of Constraint[T, C <: ConstraintAnchor], then override the assert method.

```scala
import io.github.iltotore.scalalint.constraint.ConstraintAnchor

trait Positive extends ConstraintAnchor

implicit object Positive extends Constraint[Double, Positive] {

  override def assert(value: Double): Option[String] = if (value < 0) Some("$value is not positive") else None
}
```

## Use runtime constraints

Simply use `Constrained[OriginalType, Constraint]` as your parameter's type.

```scala
import io.github.iltotore.scalalint.constraint._

def printThisNumber(number: Constrained[Double, Positive]): Unit = println(number)

printThisNumber(14)
printThisNumber(0)
printThisNumber(-3) //Runtime error
```

## Mix constraints

You can mix multiple constraints together using the `composedConstraint` method.

```scala
import io.github.iltotore.scalalint.constraint._

def printThisNumber(number: Constrained[Double, Positive & NotNull]): Unit = println(number)

given (Positive & NotNull) = composedConstraint

printThisNumber(14)
printThisNumber(0) //Runtime error
printThisNumber(-3) //Runtime error
```

## Create a compile-time constraint

Compile-time constraint creation is a bit similar to runtime constraints.

```scala
import io.github.iltotore.scalalint.constraint.ConstraintAnchor

trait Positive extends ConstraintAnchor

implicit object Positive extends CompileTimeConstraint[Double, Positive] { //We use a CompileTimeConstraint instead of a Constraint

  //Use assertCompileTime for compile-time assertions
  override inline def assertCompileTime(inline value: Double): Option[String] = if (value < 0) Some("$value is not positive") else None 
}
```

## Using compile-time constraints

Unlike runtime ones, compile-time constraints need an Inlined class. This class is like a Constrained that requires an inline parameter.

For example, this code doesn't compile at the last line.

```scala
import io.github.iltotore.scalalint.constraint._

def printThisNumber(number: Inlined[Double, Positive]): Unit = println(number)

given (Positive & NotNull) = composedConstraint

printThisNumber(14)
printThisNumber(-3)
```

Actually, the error message only shows up when passing `valueToInlined(T)` to the method. This is due to [an actual Dotty bug](https://github.com/lampepfl/dotty/issues/11386)

**Note: inlined constraints have same features than runtime ones (mixin etc...)**
