package io.github.iltotore.iron.formJsoniter

private val validJson =
  """{
    |  "name": "foo",
    |  "password": "bar123",
    |  "age": 42
    |}""".stripMargin

private val invalidJson =
  """{
    |  "name": "foo",
    |  "password": "bar",
    |  "age": 42
    |}""".stripMargin

@main def main: Unit =
  assert(Account.fromJsonString(invalidJson).isLeft)
  println(Account.fromJsonString(invalidJson))
  val account = Account.fromJsonString(validJson) // Right(Account("foo", "bar123", 42)
  val jsonString = account.map(_.asJsonString).getOrElse("")
  assert(jsonString == validJson.replace("\n", "").replace(" ", ""))
