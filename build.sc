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
}

object main extends ScalaModule with ScalafmtModule with PublishModule {

  def scalaVersion = "3.2.1-RC1" //Target 3.2.1 once out

  def artifactName = "iron"

  def publishVersion = "2.0.0-RC1"

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

  def scalaDocOptions = Seq(
    "-project", "Iron",
    "-project-version", publishVersion(),
    s"-social-links:github::${pomSettings().url}"
  )

  object test extends Tests {

    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1"
    )
  }
}

trait SubModule extends ScalaModule with ScalafmtModule with PublishModule {

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