import mill._, scalalib._, publish._

//Test core
object testCore extends ScalaModule {

  def scalaVersion = "3.0.0"

  def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.2.9")
}

trait ScalaTest extends TestModule {
  def moduleDeps = super.moduleDeps ++ Seq(testCore, main)
  def testFramework = "org.scalatest.tools.Framework"
}


//Project core
object main extends ScalaModule with PublishModule {

  def scalaVersion = "3.0.0"

  def publishVersion = "0.0.1"

  def pomSettings = PomSettings(
    description = "Type-level assertions for Scala",
    organization = "io.github.iltotore",
    url = "https://github.com/Iltotore/iron",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("Iltotore", "iron"),
    developers = Seq(
      Developer("Iltotore", "Raphaël FROMENTIN","https://github.com/Iltotore")
    )
  )

  object test extends Tests with ScalaTest
}

trait IronModule extends ScalaModule with PublishModule {

  def subVersion: String

  def scalaVersion = main.scalaVersion
  def moduleDeps = Seq(main)

  def publishVersion = s"${main.publishVersion}-$subVersion"
  def pomSettings = main.pomSettings
}


//Subprojects
object numeric extends IronModule {

  def subVersion = "0.0.1"

  def artifactName = "iron-numeric"

  object test extends Tests with ScalaTest
}