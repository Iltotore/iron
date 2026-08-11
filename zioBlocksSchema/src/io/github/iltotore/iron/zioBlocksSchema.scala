package io.github.iltotore.iron

import _root_.zio.blocks.schema.{DynamicOptic, Modifier, PrimitiveType, Reflect, Schema, SchemaError, Validation}
import _root_.zio.blocks.schema.binding.Binding
import _root_.zio.blocks.typeid.TypeId
import io.github.iltotore.iron.*

object zioBlocksSchema extends ZioBlocksSchemaLowPriority:

  given [A](using M: RefinedType.Mirror[A], S: Schema[M.IronType], typeId: TypeId[A]): Schema[A] =
    S.transform[A](_.asInstanceOf[A], _.asInstanceOf[M.IronType])

/** Controls how Iron constraints are represented in generated ZIO Blocks schemas. */
final case class ZioBlocksSchemaConfig(
    validation: ZioBlocksSchemaConfig.ValidationEncoding = ZioBlocksSchemaConfig.ValidationEncoding.EmbedSupported,
    messageMetadata: ZioBlocksSchemaConfig.MessageMetadata = ZioBlocksSchemaConfig.MessageMetadata.Modifier
)

object ZioBlocksSchemaConfig:

  /** Whether supported Iron constraints are encoded in the ZIO Blocks validation AST. */
  enum ValidationEncoding:
    /** Keep Iron's runtime decode validation only. */
    case RuntimeOnly

    /** Embed constraints with identical ZIO Blocks semantics in the schema. */
    case EmbedSupported

  /** Whether Iron's constraint message is retained as schema metadata. */
  enum MessageMetadata:
    /** Do not add the constraint message to schema metadata. */
    case Omit

    /** Add the message as `Modifier.config("iron.validation.message", message)`. */
    case Modifier

  /** The default preserves supported validations and their messages. */
  given default: ZioBlocksSchemaConfig = ZioBlocksSchemaConfig()

private trait ZioBlocksSchemaLowPriority:

  given [T, P](using
      S: Schema[T],
      C: RuntimeConstraint[T, P],
      V: ZioBlocksValidation[T, P],
      config: ZioBlocksSchemaConfig,
      typeId: TypeId[T :| P]
  ): Schema[T :| P] =
    val base = config.validation match
      case ZioBlocksSchemaConfig.ValidationEncoding.EmbedSupported => V.validation.fold(S)(ZioBlocksSchemaSupport.embed(S, _))
      case ZioBlocksSchemaConfig.ValidationEncoding.RuntimeOnly    => S

    val refined = base.transform[T :| P](
      _.refineEither[P].fold(error => throw SchemaError.validationFailed(error), identity),
      identity
    )

    config.messageMetadata match
      case ZioBlocksSchemaConfig.MessageMetadata.Modifier if C.message.nonEmpty =>
        refined.modifier(Modifier.config("iron.validation.message", C.message))
      case _ => refined

private object ZioBlocksSchemaSupport:

  def embed[A](schema: Schema[A], validation: Validation[A]): Schema[A] =
    schema
      .updated(DynamicOptic.root)(new Reflect.Updater[Binding]:
        def update[B](reflect: Reflect[Binding, B]): Reflect[Binding, B] =
          reflect match
            case primitive: Reflect.Primitive[Binding, B] =>
              primitive.copy(
                primitiveType = withValidation(primitive.primitiveType, validation.asInstanceOf[Validation[B]]),
                typeId = primitive.typeId,
                primitiveBinding = primitive.primitiveBinding,
                doc = primitive.doc,
                modifiers = primitive.modifiers,
                storedDefaultValue = primitive.storedDefaultValue,
                storedExamples = primitive.storedExamples
              )
            case other => other)
      .getOrElse(schema)

  private def withValidation[A](primitiveType: PrimitiveType[A], validation: Validation[A]): PrimitiveType[A] =
    (primitiveType match
      case _: PrimitiveType.Int        => PrimitiveType.Int(validation.asInstanceOf[Validation[Int]])
      case _: PrimitiveType.Long       => PrimitiveType.Long(validation.asInstanceOf[Validation[Long]])
      case _: PrimitiveType.Float      => PrimitiveType.Float(validation.asInstanceOf[Validation[Float]])
      case _: PrimitiveType.Double     => PrimitiveType.Double(validation.asInstanceOf[Validation[Double]])
      case _: PrimitiveType.BigInt     => PrimitiveType.BigInt(validation.asInstanceOf[Validation[BigInt]])
      case _: PrimitiveType.BigDecimal => PrimitiveType.BigDecimal(validation.asInstanceOf[Validation[BigDecimal]])
      case _: PrimitiveType.String     => PrimitiveType.String(validation.asInstanceOf[Validation[String]])
      case _                           => primitiveType
    ).asInstanceOf[PrimitiveType[A]]
