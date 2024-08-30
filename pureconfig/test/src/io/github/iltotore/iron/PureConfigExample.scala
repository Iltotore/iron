package io.github.iltotore.iron

import _root_.pureconfig.ConfigReader
import _root_.pureconfig.generic.derivation.default.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.pureconfig.given

opaque type Username = String :| MinLength[5]
object Username extends RefinedTypeOps[String, MinLength[5], Username]

case class IronTypeConfig(
    username: String :| MinLength[5]
) derives ConfigReader

case class NewTypeConfig(
    username: Username
) derives ConfigReader
