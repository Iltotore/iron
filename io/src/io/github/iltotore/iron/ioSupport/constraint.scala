package io.github.iltotore.iron.ioSupport

import io.github.iltotore.iron.==>
import io.github.iltotore.iron.constraint.*

import java.io.File
import java.nio.file.{Files, Path, Paths}

object constraint {

  /**
   * Constraint: checks if the input (supported by default: File, Path, String) exists.
   */
  trait Exists

  given Constraint.RuntimeOnly[File, Exists] with {

    override inline def assert(value: File): Boolean = value.exists

    override inline def getMessage(value: File): String = s"No such file or directoy: ${value.getPath()}"
  }

  given Constraint.RuntimeOnly[Path, Exists] with {

    override inline def assert(value: Path): Boolean = Files.exists(value)

    override inline def getMessage(value: Path): String = s"No such file or directoy: $value"
  }

  given Constraint.RuntimeOnly[String, Exists] with {

    override inline def assert(value: String): Boolean = Files.exists(Paths.get(value))

    override inline def getMessage(value: String): String = s"No such file or directoy: $value"
  }
}
