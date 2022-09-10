import mill._, scalalib._, scalalib.scalafmt._, scalalib.publish._

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

}