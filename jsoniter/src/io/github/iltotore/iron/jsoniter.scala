package io.github.iltotore.iron

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

object jsoniter:

  /**
   * Creates a [[JsonValueCodec]] for refined types
   *
   * @param constraint the [[Constraint]] implementation to test the decoded value.
   */
  inline given makeCodec[A, B](using
      inline constraint: Constraint[A, B],
      inline config: CodecMakerConfig = CodecMakerConfig
  ): JsonValueCodec[A :| B] = new:
    private val codec = JsonCodecMaker.make[A](config)
    def decodeValue(in: JsonReader, default: A :| B): A :| B =
      val decoded = codec.decodeValue(in, default)
      if constraint.test(decoded) then decoded.asInstanceOf[A :| B]
      else in.decodeError(constraint.message)
    def encodeValue(x: A :| B, out: JsonWriter): Unit = codec.encodeValue(x, out)
    def nullValue: A :| B = null.asInstanceOf[A :| B]

  /**
   * Creates a [[JsonValueCodec]] for new types
   */
  inline given refinedTypeCodec[T](using mirror: RefinedType.Mirror[T], ev: JsonValueCodec[mirror.IronType]): JsonValueCodec[T] =
    ev.asInstanceOf[JsonValueCodec[T]]

  /**
   * Creates a [[JsonKeyCodec]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param codec      the [[JsonKeyCodec]] of the underlying type.
   * @param constraint the [[Constraint]] implementation to test the decoded key.
   */
  inline given makeKeyCodec[A, B](using codec: JsonKeyCodec[A], inline constraint: Constraint[A, B]): JsonKeyCodec[A :| B] = new:
    def decodeKey(in: JsonReader): A :| B =
      val decoded = codec.decodeKey(in)
      if constraint.test(decoded) then decoded.asInstanceOf[A :| B]
      else in.decodeError(constraint.message)
    def encodeKey(x: A :| B, out: JsonWriter): Unit = codec.encodeKey(x, out)

  /**
   * Creates a [[JsonKeyCodec]] for new types
   */
  inline given refinedTypeKeyCodec[T](using mirror: RefinedType.Mirror[T], ev: JsonKeyCodec[mirror.IronType]): JsonKeyCodec[T] =
    ev.asInstanceOf[JsonKeyCodec[T]]
