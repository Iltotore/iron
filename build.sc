import mill._
import api.{PathRef, Result}
import modules.Jvm.createJar
import scalalib._
import scalalib.api.Util.{isDotty, isScala3, isScala3Milestone}
import publish._
import os.pwd

//Test core
object testCore extends ScalaModule {

  def scalaVersion = "3.0.0"

  def moduleDeps = super.moduleDeps :+ main
  def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.2.9")
}

trait ScalaTest extends TestModule {
  def moduleDeps = super.moduleDeps ++ Seq(testCore, main)
  def testFramework = "org.scalatest.tools.Framework"
}


//Project core
object main extends ScalaModule with PublishModule {

  def scalaVersion = "3.0.2"

  def publishVersion = "1.1.2"

  def majorVersion: T[String] = publishVersion()
    .split("\\.")
    .slice(0, 2)
    .iterator.mkString(".")

  def artifactName = "iron"

  def pomSettings = PomSettings(
    description = "Hardened type constraints for Scala",
    organization = "io.github.iltotore",
    url = "https://github.com/Iltotore/iron",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("Iltotore", "iron"),
    developers = Seq(
      Developer("Iltotore", "Raphaël FROMENTIN", "https://github.com/Iltotore")
    )
  )

  def docSources = T.sources(pwd / "extra")

  def fullScaladocModules: Agg[ScalaModule] = Agg(
    main,
    cats,
    circe,
    iterable,
    numeric,
    string,
    io
  )

  def fullScaladocClasspath = T {
    T.sequence(fullScaladocModules.map(_.compileClasspath).iterator.toSeq)()
      .flatten
      .filter(_.path.ext != "pom")
      .map(_.path)
  }

  def fullScaladocFiles = T {
    T.sequence(fullScaladocModules.map(_.compile).iterator.toSeq)()
      .flatMap(result => os.walk(result.classes.path))
      .filter(_.ext == "tasty")
      .map(_.toString)
  }

  //Rewrite of Mill's docJar to support custom scaladoc source
  def fullDocJar: T[PathRef] = T {
    val pluginOptions = scalaDocPluginClasspath().map(pluginPathRef =>
      s"-Xplugin:${pluginPathRef.path}")
    val compileCp = Seq("-classpath", fullScaladocClasspath().mkString(java.io.File.pathSeparator))

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
      fullScaladocFiles().iterator.toSeq,
      javadocDir
    )
  }

  object test extends Tests with ScalaTest
}

trait IronModule extends ScalaModule with PublishModule {

  def subVersion: String

  def scalaVersion = main.scalaVersion
  def moduleDeps: Seq[ScalaModule with PublishModule] = Seq(main)

  def publishVersion = s"${main.majorVersion()}-$subVersion"
  def artifactName = s"iron-${super.artifactName()}"
  def pomSettings = main.pomSettings

  def docSources = T.sources(pwd / "extra")
}


//Subprojects
object numeric extends IronModule {

  def subVersion = "1.0.1"

  object test extends Tests with ScalaTest
}

object string extends IronModule {

  def subVersion = "1.0.0"

  object test extends Tests with ScalaTest
}

object iterable extends IronModule {

  def subVersion = "1.0.0"

  object test extends Tests with ScalaTest
}

object cats extends IronModule {

  def subVersion = "1.0.0"

  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"org.typelevel::cats-core:2.6.1"
  )

  object test extends Tests with ScalaTest
}

object circe extends IronModule {

  def subVersion = "1.0.0"

  def ivyDeps = super.ivyDeps() ++ Agg(
    ivy"io.circe::circe-core:0.14.1",
    ivy"io.circe::circe-parser:0.14.1",
    ivy"io.circe::circe-generic:0.14.1"
  )

  object test extends Tests with ScalaTest
}

object io extends IronModule {

  def subVersion = "1.0.0"

  object test extends Tests with ScalaTest
}