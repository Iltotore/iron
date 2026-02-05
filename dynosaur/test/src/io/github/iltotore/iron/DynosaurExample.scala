package io.github.iltotore.iron

import dynosaur.given
import _root_.dynosaur.Schema
import _root_.dynosaur.DynamoValue
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import software.amazon.awssdk.services.dynamodb.model.AttributeValue as JAttributeValue

import java.util.Map as JMap
import cats.syntax.all.*

// defining a type with IronType
type FirstName = String :| Not[Blank]

// defining a type with RefinedType
type LastName = LastName.T
object LastName extends RefinedType[String, Not[Blank]]

// defining a type with RefinedSubtype
type Age = Age.T
object Age extends RefinedSubtype[Int, Positive]

// defining a case class that represents a decodable/encodable DynamoDB record
final case class Person(firstName: FirstName, lastName: LastName, age: Age)
object Person:
  // defining a Schema[Person] instance of the Person type
  given Schema[Person] =
    Schema.record: r =>
      (
        r("first_name", _.firstName),
        r("last_name", _.lastName),
        r("age", _.age)
      ).mapN(Person.apply)

  val validJDynamoValue: DynamoValue =
    DynamoValue(
      JAttributeValue.fromM(
        JMap.of(
          "first_name",
          JAttributeValue.fromS("iron"),
          "last_name",
          JAttributeValue.fromS("dynosaur"),
          "age",
          JAttributeValue.fromN("1")
        )
      )
    )

  val invalidJDynamoValue: DynamoValue =
    DynamoValue(
      JAttributeValue.fromM(
        JMap.of(
          "first_name",
          JAttributeValue.fromS("iron"),
          "last_name",
          JAttributeValue.fromS("dynosaur"),
          "age",
          JAttributeValue.fromN("0") // 0 does not satisfy Positive constraint
        )
      )
    )

object DynosaurExample extends App:

  println(Schema[Person].read(Person.validJDynamoValue)) // Right(Person(firstName = "iron", lastName = "dynosaur", age = 1))
  println(Schema[Person].read(Person.invalidJDynamoValue)) // Left(ReadError("Should be greater than 0"))
