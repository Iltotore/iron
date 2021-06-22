import mill._
import mill.api.{PathRef, Result}
import mill.modules.Jvm.createJar
import mill.scalalib.api.Util.{isDotty, isScala3, isScala3Milestone}
import scalalib._
import publish._

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

  def scaladocFiles = T {
    os.walk(compile().classes.path) ++ os.walk(numeric.compile().classes.path)
  }

  //Rewrite of Mill's docJar to support custom scaladoc source
  def docJar: T[PathRef] = T {
    val pluginOptions = scalaDocPluginClasspath().map(pluginPathRef =>
      s"-Xplugin:${pluginPathRef.path}")
    val compileCp = Seq(
      "-classpath",
      compileClasspath()
        .filter(_.path.ext != "pom")
        .map(_.path)
        .mkString(java.io.File.pathSeparator)
    )

    def packageWithZinc(options: Seq[String],
                        files: Seq[String],
                        javadocDir: os.Path) = {
      if (files.isEmpty) Result.Success(createJar(Agg(javadocDir))(T.dest))
      else {
        if (zincWorker
          .worker()
          .docJar(
            scalaVersion(),
            scalaOrganization(),
            scalaDocClasspath().map(_.path),
            scalacPluginClasspath().map(_.path),
            files ++ options ++ pluginOptions ++ compileCp ++ scalaDocOptions()
          )) {
          Result.Success(createJar(Agg(javadocDir))(T.dest))
        } else {
          Result.Failure("docJar generation failed")
        }
      }
    }

    if (isDotty(scalaVersion()) || isScala3Milestone(scalaVersion())) { // dottydoc
      val javadocDir = T.dest / "javadoc"
      os.makeDir.all(javadocDir)

      for {
        ref <- docSources()
        docSource = ref.path
        if os.exists(docSource) && os.isDir(docSource)
        children = os.walk(docSource)
        child <- children
        if os.isFile(child)
      } {
        os.copy.over(
          child,
          javadocDir / (child.subRelativeTo(docSource)),
          createFolders = true)
      }
      packageWithZinc(
        Seq("-siteroot", javadocDir.toNIO.toString),
        allSourceFiles().map(_.path.toString),
        javadocDir / "_site"
      )

    } else if (isScala3(scalaVersion())) { // scaladoc 3
      val javadocDir = T.dest / "javadoc"
      os.makeDir.all(javadocDir)

      // Scaladoc 3 allows including static files in documentation, but it only
      // supports one directory. Hence, to allow users to generate files
      // dynamically, we consolidate all files from all `docSources` into one
      // directory.
      val combinedStaticDir = T.dest / "static"
      os.makeDir.all(combinedStaticDir)

      for {
        ref <- docSources()
        docSource = ref.path
        if os.exists(docSource) && os.isDir(docSource)
        children = os.walk(docSource)
        child <- children
        if os.isFile(child)
      } {
        os.copy.over(
          child,
          combinedStaticDir / (child.subRelativeTo(docSource)),
          createFolders = true)
      }

      packageWithZinc(
        Seq(
          "-d",
          javadocDir.toNIO.toString,
          "-siteroot",
          combinedStaticDir.toNIO.toString
        ),
        scaladocFiles()
          .filter(_.ext == "tasty")
          .map(_.toString),
        javadocDir
      )
    } else { // scaladoc 2
      val javadocDir = T.dest / "javadoc"
      os.makeDir.all(javadocDir)

      packageWithZinc(
        Seq("-d", javadocDir.toNIO.toString),
        allSourceFiles().map(_.path.toString),
        javadocDir
      )
    }

  }

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