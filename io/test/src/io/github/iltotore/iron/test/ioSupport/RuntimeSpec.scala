package io.github.iltotore.iron.test.ioSupport

import io.github.iltotore.iron.*, constraint.*, ioSupport.constraint.*
import io.github.iltotore.iron.test.UnitSpec

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardCopyOption}

class RuntimeSpec extends UnitSpec {

  {
    val foo = Files.createDirectories(Paths.get("tmp")).resolve("foo")
    if (Files.notExists(foo)) Files.createFile(foo)
  }

  "An Exists constraint" should "return Right if the given File exists" in {

    def dummy(x: File ==> Exists): File ==> Exists = x

    assert(dummy(new File("tmp/foo")).isRight)
    assert(dummy(new File("tmp/foo2")).isLeft)
  }

  it should "return Right if the given Path exists" in {

    def dummy(x: Path ==> Exists): Path ==> Exists = x

    assert(dummy(Paths.get("tmp", "foo")).isRight)
    assert(dummy(Paths.get("tmp", "foo2")).isLeft)
  }

  it should "return Right if the given String exists" in {

    def dummy(x: String ==> Exists): String ==> Exists = x

    assert(dummy("tmp/foo").isRight)
    assert(dummy("tmp/foo2").isLeft)
  }
}