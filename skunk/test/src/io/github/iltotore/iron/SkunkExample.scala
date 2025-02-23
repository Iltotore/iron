package io.github.iltotore.iron

import _root_.skunk.*
import _root_.skunk.implicits.*
import _root_.skunk.codec.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.skunk.*
import io.github.iltotore.iron.skunk.given

type Username = String :| Not[Blank]

// refining a codec at usage site
val a: Query[Void, Username] = sql"SELECT name FROM users".query(varchar.refined)

// defining a codec for a refined opaque type
type PositiveInt = PositiveInt.T
object PositiveInt extends RefinedType[Int, Positive]:
  given codec: Codec[PositiveInt] = int4.refined[Positive].imap(assume)(_.value)

// defining a codec for a refined case class
final case class User(name: Username, age: PositiveInt)
given Codec[User] = (varchar.refined[Not[Blank]] *: PositiveInt.codec).to[User]
