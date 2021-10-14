package io.github.iltotore.iron.test.ioSupport

import io.github.iltotore.iron.*, constraint.*, ioSupport.constraint.*
import io.github.iltotore.iron.test.UnitSpec

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardCopyOption}

class RuntimeSpec extends UnitSpec {

  {
    val tmp = Files.createDirectories(Paths.get("tmp"))
    val foo = tmp.resolve("foo")
    val fooDir = tmp.resolve("fooDir")
    if(Files.notExists(foo)) Files.createFile(foo)
    if(Files.notExists(fooDir)) Files.createDirectory(fooDir)
  }

  "An Exists constraint" should "return Right if the given File exists" in {

    def dummy(x: File / Exists): File / Exists = x

    assert(dummy(new File("tmp/foo")).isRight)
    assert(dummy(new File("tmp/foo2")).isLeft)
  }

  it should "return Right if the given Path exists" in {

    def dummy(x: Path / Exists): Path / Exists = x

    assert(dummy(Paths.get("tmp", "foo")).isRight)
    assert(dummy(Paths.get("tmp", "foo2")).isLeft)
  }

  it should "return Right if the given String exists" in {

    def dummy(x: String / Exists): String / Exists = x

    assert(dummy("tmp/foo").isRight)
    assert(dummy("tmp/foo2").isLeft)
  }

  "An IsFile constraint" should "return Right if the given File is a regular file" in {

    def dummy(x: File / IsFile): File / IsFile = x

    assert(dummy(new File("tmp/foo")).isRight)
    assert(dummy(new File("tmp/fooDir")).isLeft)
  }

  it should "return Right if the given Path is a regular file" in {

    def dummy(x: Path / IsFile): Path / IsFile = x

    assert(dummy(Paths.get("tmp", "foo")).isRight)
    assert(dummy(Paths.get("tmp", "fooDir")).isLeft)
  }

  it should "return Right if the given String is a regular file" in {

    def dummy(x: String / IsFile): String / IsFile = x

    assert(dummy("tmp/foo").isRight)
    assert(dummy("tmp/fooDir").isLeft)
  }

  "An IsDirectory constraint" should "return Right if the given File is a directory" in {

    def dummy(x: File / IsDirectory): File / IsDirectory = x

    assert(dummy(new File("tmp/fooDir")).isRight)
    assert(dummy(new File("tmp/foo")).isLeft)
  }

  it should "return Right if the given Path is a directory" in {

    def dummy(x: Path / IsDirectory): Path / IsDirectory = x

    assert(dummy(Paths.get("tmp", "fooDir")).isRight)
    assert(dummy(Paths.get("tmp", "foo")).isLeft)
  }

  it should "return Right if the given String is a directory" in {

    def dummy(x: String / IsDirectory): String / IsDirectory = x

    assert(dummy("tmp/fooDir").isRight)
    assert(dummy("tmp/foo").isLeft)
  }
}