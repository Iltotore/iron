package io.github.iltotore.iron

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*

object jsoniter:

  /**
   * Creates a [[JsonValueCodec]] for refined types
   *
   * @param constraint the [[Constraint]] implementation to test the decoded value.
   */
  inline given makeCodec[A, B](using inline constraint: Constraint[A, B]): JsonValueCodec[A :| B] = new:
    private val codec = JsonCodecMaker.make[A]
    def decodeValue(in: JsonReader, default: A :| B): A :| B = codec.decodeValue(in, default).refineEither[B].fold(in.decodeError, identity)
    def encodeValue(x: A :| B, out: JsonWriter): Unit = codec.encodeValue(x, out)
    def nullValue: A :| B = null.asInstanceOf[A :| B]
