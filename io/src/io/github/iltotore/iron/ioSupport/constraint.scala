package io.github.iltotore.iron.ioSupport

import io.github.iltotore.iron./
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

  /**
   * Constraint: checks if the input is a regular file.
   */
  trait IsFile

  given Constraint.RuntimeOnly[File, IsFile] with {

    override inline def assert(value: File): Boolean = value.isFile

    override inline def getMessage(value: File): String = s"Not a file: ${value.getPath}"
  }

  given Constraint.RuntimeOnly[Path, IsFile] with {

    override inline def assert(value: Path): Boolean = Files.isRegularFile(value)

    override inline def getMessage(value: Path): String = s"Not a file: $value"
  }

  given Constraint.RuntimeOnly[String, IsFile] with {

    override inline def assert(value: String): Boolean = Files.isRegularFile(Paths.get(value))

    override inline def getMessage(value: String): String = s"Not a file: $value"
  }

  /**
   * Constraint: checks if the input is a directory.
   */
  trait IsDirectory

  given Constraint.RuntimeOnly[File, IsDirectory] with {

    override inline def assert(value: File): Boolean = value.isDirectory()

    override inline def getMessage(value: File): String = s"Not a directory: ${value.getPath}"
  }

  given Constraint.RuntimeOnly[Path, IsDirectory] with {

    override inline def assert(value: Path): Boolean = Files.isDirectory(value)

    override inline def getMessage(value: Path): String = s"Not a directory: $value"
  }

  given Constraint.RuntimeOnly[String, IsDirectory] with {

    override inline def assert(value: String): Boolean = Files.isDirectory(Paths.get(value))

    override inline def getMessage(value: String): String = s"Not a directory: $value"
  }
}
