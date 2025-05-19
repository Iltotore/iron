package io.github.iltotore.iron

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

object jsoniter:

  /**
   * Creates a [[JsonValueCodec]] for refined types
   *
   * @param constraint the [[Constraint]] implementation to test the decoded value.
   */
  inline given makeCodec[A, B](using inline constraint: Constraint[A, B], inline config: CodecMakerConfig = CodecMakerConfig): JsonValueCodec[A :| B] = new:
    private val codec = JsonCodecMaker.make[A](config)
    def decodeValue(in: JsonReader, default: A :| B): A :| B =
      val decoded = codec.decodeValue(in, default)
      if constraint.test(decoded) then decoded.asInstanceOf[A :| B]
      else in.decodeError(constraint.message)
    def encodeValue(x: A :| B, out: JsonWriter): Unit = codec.encodeValue(x, out)
    def nullValue: A :| B = null.asInstanceOf[A :| B]
