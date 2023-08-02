package io.github.iltotore.iron

import _root_.ciris.ConfigDecoder
import cats.Show


object ciris:

  inline given [T,A,B](using inline decoder: ConfigDecoder[T,A], inline constraint: _root_.io.github.iltotore.iron.Constraint[A, B], inline show: Show[A]): ConfigDecoder[T, A :| B] =
    decoder.mapOption("")(_.refineOption)

