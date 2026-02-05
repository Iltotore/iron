package io.github.iltotore.iron

import _root_.dynosaur.Schema
import _root_.dynosaur.Schema.ReadError
import io.github.iltotore.iron.*

object dynosaur:

  given [T, P](using Schema[T], RuntimeConstraint[T, P]): Schema[T :| P] =
    Schema[T].imapErr[T :| P](_.refineEither[P].left.map(ReadError.apply))(identity)

  given [A](using M: RefinedType.Mirror[A], S: Schema[M.IronType]): Schema[A] =
    S.asInstanceOf[Schema[A]]
