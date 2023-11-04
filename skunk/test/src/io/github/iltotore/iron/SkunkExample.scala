package io.github.iltotore.iron

import _root_.skunk.*
import _root_.skunk.implicits.*
import _root_.skunk.codec.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.skunk.*
import io.github.iltotore.iron.skunk.given

type Username = String :| Not[Blank]

// refine a codec implicitly
val a: Query[Void, Username] = sql"SELECT name FROM users".query(varchar)

// refine a codec explictly
val b: Query[Void, Username] = sql"SELECT name FROM users".query(varchar.refined)

// defining a codec for a refined case class
final case class User(name: Username, age: Int :| Positive)
given Codec[User] = (varchar.refined[Not[Blank]] *: int4.refined[Positive]).to[User]
