import $ivy.`io.chris-kipp::mill-ci-release::0.1.5`
import io.kipp.mill.ci.release.CiReleaseModule

import mill._, define._
import scalalib._, scalalib.scalafmt._, scalalib.publish._, scalajslib._, scalanativelib._

object versions {
  val scala = "3.2.1"
  val scalaJS = "1.12.0"
  val scalaNative = "0.4.10"
}

trait BaseModule extends ScalaModule with ScalafmtModule with CiReleaseModule { outer =>

  def scalaVersion = versions.scala

  def pomSettings =
    PomSettings(
      description = "Strong type constraints for Scala",
      organization = "io.github.iltotore",
      url = "https://github.com/Iltotore/iron",
      licenses = Seq(License.`Apache-2.0`),
      versionControl = VersionControl.github("Iltotore", "iron"),
      developers = Seq(
        Developer("Iltotore", "Raphaël FROMENTIN", "https://github.com/Iltotore")
      )
    )

  trait Tests extends super.Tests with ScalafmtModule {

    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1"
    )
  }

  trait CrossModule extends ScalaModule with ScalafmtModule with CiReleaseModule  {

    def segment: String

    def sources = T.sources(outer.sources() :+ PathRef(millSourcePath / s"src-$segment"))

    def scalaVersion = outer.scalaVersion

    def ivyDeps = outer.ivyDeps

    def artifactName = outer.artifactName

    def publishVersion = outer.publishVersion

    def pomSettings = outer.pomSettings

  }

  trait JSCrossModule extends CrossModule with ScalaJSModule {

    def segment = "js"

    def scalaJSVersion = versions.scalaJS

  }

  trait NativeCrossModule extends CrossModule with ScalaNativeModule {

    def segment = "native"

    def scalaNativeVersion = versions.scalaNative
  }
}

object docs extends ScalaModule {

  def scalaVersion = versions.scala

  val modules: Seq[ScalaModule] = Seq(main, cats, circe, jsoniter, scalacheck, zio, zioJson)

  def docSources = T.sources {
    T.traverse(modules)(_.docSources)().flatten
  }

  def compileClasspath = T {
    T.traverse(modules)(_.compileClasspath)().flatten
  }

  def docResources = T.sources { millSourcePath }

  def scalaDocOptions = Seq(
    "-project", "Iron",
    "-project-version", main.publishVersion(),
    s"-social-links:github::${main.pomSettings().url}"
  )
}

object main extends BaseModule {

  def artifactName = "iron"

  object test extends Tests

  object js extends JSCrossModule
  object native extends NativeCrossModule
}

object examples extends Module {

  object catsValidation extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, cats)

    def ivyDeps = Agg(
      ivy"org.typelevel::cats-effect:3.4.2"
    )
  }

  object formCats extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, cats, circe)

    val circeVersion = "0.14.3"
    val http4sVersion = "0.23.16"

    def ivyDeps = Agg(
      ivy"org.typelevel::cats-core:2.8.0",
      ivy"io.circe::circe-core:$circeVersion",
      ivy"io.circe::circe-parser:$circeVersion",
      ivy"io.circe::circe-generic:$circeVersion",
      ivy"org.http4s::http4s-core:$http4sVersion",
      ivy"org.http4s::http4s-dsl:$http4sVersion",
      ivy"org.http4s::http4s-ember-server:$http4sVersion",
      ivy"org.http4s::http4s-circe:$http4sVersion",
    )
  }

  object formZio extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, zioJson)

    def ivyDeps = Agg(
      ivy"dev.zio::zio-test:2.0.4",
      ivy"dev.zio::zio-json:0.3.0",
      ivy"dev.zio::zio-http:0.0.3"
    )

  }

  object formJsoniter extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, jsoniter)

    val jsoniterVersion = "2.19.1"

    def ivyDeps = Agg(
      ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core:$jsoniterVersion",
    )

    def compileIvyDeps = Agg(
      ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:$jsoniterVersion"
    )
  }
}

trait SubModule extends BaseModule {

  def moduleDeps = Seq(main)

  trait JSCrossModule extends super.JSCrossModule {
    def moduleDeps = Seq(main.js)
  }

  trait NativeCrossModule extends super.NativeCrossModule {

    def transitiveIvyDeps = T { super.transitiveIvyDeps().filter(d => !(d.dep.module.name.value == "scala3-library")) }

    def moduleDeps = Seq(main.native)
  }

}

object cats extends SubModule {

  def artifactName = "iron-cats"

  def ivyDeps = Agg(
    ivy"org.typelevel::cats-core::2.8.0"
  )

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"org.typelevel::kittens:3.0.0"
    )
  }

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object circe extends SubModule {

  def artifactName = "iron-circe"

  def ivyDeps = Agg(
    ivy"io.circe::circe-core::0.14.3"
  )

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object zio extends SubModule {

  def artifactName = "iron-zio"

  def ivyDeps = Agg(
    ivy"dev.zio::zio::2.0.5",
    ivy"dev.zio::zio-prelude::1.0.0-RC16"
  )

  object js extends JSCrossModule

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1"
    )
  }

}

object zioJson extends SubModule {

  def artifactName = "iron-zio-json"

  def ivyDeps = Agg(
    ivy"dev.zio::zio-json::0.3.0"
  )

  object js extends JSCrossModule
}

object jsoniter extends SubModule {

  def artifactName = "iron-jsoniter"

  val jsoniterVersion = "2.19.1"

  private val jsoniterMacros = ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-macros:$jsoniterVersion"

  def ivyDeps = Agg(
    ivy"com.github.plokhotnyuk.jsoniter-scala::jsoniter-scala-core::$jsoniterVersion"
  )

  def compileIvyDeps = Agg(jsoniterMacros)

  object js extends JSCrossModule {
    def compileIvyDeps = Agg(jsoniterMacros)
  }

  object native extends NativeCrossModule {
    def compileIvyDeps = Agg(jsoniterMacros)
  }

  object test extends Tests {
    def compileIvyDeps = Agg(jsoniterMacros)
  }
}

object scalacheck extends SubModule {

  def artifactName = "iron-scalacheck"

  def ivyDeps = Agg(
    ivy"org.scalacheck::scalacheck::1.17.0"
  )

  object js extends JSCrossModule

  object test extends Tests
}