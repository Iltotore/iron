package io.github.iltotore.iron.internal

import scala.util.NotGiven

private[iron] type NotNothing[A] = NotGiven[A <:< Nothing]
