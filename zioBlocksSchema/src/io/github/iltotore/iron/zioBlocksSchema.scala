package io.github.iltotore.iron

import _root_.zio.blocks.schema.{Schema, SchemaError}
import _root_.zio.blocks.typeid.TypeId
import io.github.iltotore.iron.*

object zioBlocksSchema extends ZioBlocksSchemaLowPriority:

  given [A](using M: RefinedType.Mirror[A], S: Schema[M.IronType], typeId: TypeId[A]): Schema[A] =
    S.transform[A](_.asInstanceOf[A], _.asInstanceOf[M.IronType])

private trait ZioBlocksSchemaLowPriority:

  given [T, P](using S: Schema[T], C: RuntimeConstraint[T, P], typeId: TypeId[T :| P]): Schema[T :| P] =
    S.transform[T :| P](
      _.refineEither[P].fold(error => throw SchemaError.validationFailed(error), identity),
      identity
    )
