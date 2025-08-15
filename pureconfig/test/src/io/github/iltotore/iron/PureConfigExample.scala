package io.github.iltotore.iron

import _root_.pureconfig.ConfigReader
import _root_.pureconfig.generic.derivation.default.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.pureconfig.given

type Username = Username.T
object Username extends RefinedType[String, MinLength[5]]

type Password = Password.T
object Password extends RefinedSubtype[String, MinLength[5]]

case class IronTypeConfig(
    username: String :| MinLength[5]
) derives ConfigReader

case class NewTypeConfig(
    username: Username,
    password: Password
) derives ConfigReader
