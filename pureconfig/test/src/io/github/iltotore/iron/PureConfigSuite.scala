package io.github.iltotore.iron

import _root_.pureconfig.ConfigSource
import _root_.pureconfig.generic.derivation.default.*
import utest.*

object PureConfigSuite extends TestSuite:

  val tests: Tests = Tests:

    test("reader"):
      test("ironType"):
        test("success") - assert(ConfigSource.string("{ username: admin }").load[IronTypeConfig] == Right(IronTypeConfig("admin")))
        test("failure") - assert(ConfigSource.string("{ username: a }").load[IronTypeConfig].isLeft)

      test("newType"):
        test("success") - assert(ConfigSource.string("{ username: admin, password: password }").load[NewTypeConfig] == Right(NewTypeConfig(
          Username("admin"),
          Password("password")
        )))
        test("failure") - assert(ConfigSource.string("{ username: a }").load[NewTypeConfig].isLeft)
        test("failure") - assert(ConfigSource.string("{ username: admin, password: p }").load[NewTypeConfig].isLeft)
