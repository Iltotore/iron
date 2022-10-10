import io.github.iltotore.iron.*, catsModule.*, constraint.numeric.{given, *}, constraint.string.{given, *}
import cats.implicits.*
import cats.data.ValidatedNec

object Main:

  case class User(name: String :| Alphanumeric, age: Int :| Greater[0])

  def createUserAcc(name: String, age: Int): ValidatedNec[String, User] =
    (
      name.refineNec[Alphanumeric],
      age.refineNec[Greater[0]]
    ).mapN(User.apply)

  @main def testMain =
    val name = "Iltotore"
    val age = 18

    println(createUserAcc(name, age))
