import $ivy.`io.chris-kipp::mill-ci-release::0.1.3`
import io.kipp.mill.ci.release.CiReleaseModule

import mill._, define._, scalalib._, scalalib.scalafmt._, scalalib.publish._

object docs extends ScalaModule {

  def scalaVersion = "3.2.1-RC1"

  val modules: Seq[ScalaModule] = Seq(main, cats, circe)

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

object main extends ScalaModule with ScalafmtModule with CiReleaseModule {

  def scalaVersion = "3.2.1"

  def artifactName = "iron"

  def pomSettings = PomSettings(
    description = "Strong type constraints for Scala",
    organization = "io.github.iltotore",
    url = "https://github.com/Iltotore/iron",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("Iltotore", "iron"),
    developers = Seq(
      Developer("Iltotore", "Raphaël FROMENTIN", "https://github.com/Iltotore")
    )
  )

  object test extends Tests with ScalafmtModule {

    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1"
    )
  }
}

object examples extends Module {

  object formCats extends ScalaModule with ScalafmtModule {

    def scalaVersion = main.scalaVersion

    def moduleDeps = Seq(main, cats, circe)

    def ivyDeps = Agg(
      ivy"org.typelevel::cats-core:2.8.0",
      ivy"io.circe::circe-core:0.14.3",
      ivy"io.circe::circe-parser:0.14.3",
      ivy"io.circe::circe-generic:0.14.3",
      ivy"org.http4s::http4s-core:0.23.16"
    )
  }
}

trait SubModule extends ScalaModule with ScalafmtModule with CiReleaseModule {

  def scalaVersion = main.scalaVersion

  def publishVersion = main.publishVersion

  def pomSettings = main.pomSettings

  def moduleDeps = Seq(main)
}

object cats extends SubModule {

  def artifactName = "iron-cats"

  def ivyDeps = Agg(
    ivy"org.typelevel::cats-core:2.8.0"
  )
}

object circe extends SubModule {

  def artifactName = "iron-circe"

  def ivyDeps = Agg(
    ivy"io.circe::circe-core:0.14.3",
    ivy"io.circe::circe-parser:0.14.3",
    ivy"io.circe::circe-generic:0.14.3"
  )
}

object zioJson extends SubModule {

  def artifactName = "iron-zio-json"

  def ivyDeps = Agg(
    ivy"dev.zio::zio-json:0.3.0"
  )
}