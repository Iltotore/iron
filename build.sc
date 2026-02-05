import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.4.1`
import de.tobiasroeser.mill.vcs.version.VcsVersion

import mill._, define._, api.Result
import scalalib._, scalalib.scalafmt._, scalalib.publish._, scalajslib._, scalanativelib._

object versions {
  val scala = "3.3.6"
  val scalaJS = "1.16.0"
  val scalaNative = "0.5.7"
}

trait BaseModule extends ScalaModule with ScalafmtModule with SonatypeCentralPublishModule { outer =>

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

  def publishVersion: T[String] = T {
    VcsVersion.vcsState().format(untaggedSuffix = "-SNAPSHOT")
  }

  trait Tests extends ScalaTests with ScalafmtModule {

    def testFramework = "utest.runner.Framework"

    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1"
    )
  }

  trait CrossModule extends ScalaModule with ScalafmtModule with SonatypeCentralPublishModule  {

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

  trait NativeCrossModule04 extends CrossModule with ScalaNativeModule {

    def segment = "native"

    def scalaNativeVersion = "0.4.17"
  }
}

object docs extends BaseModule {

  def scalaVersion = versions.scala

  def artifactName = "iron-docs"

  val modules: Seq[ScalaModule] =
    Seq(main, cats, chimney, circe, decline, doobie, dynosaur, upickle, ciris, jsoniter, pureconfig, scalacheck, scodec, skunk, upickle, zio, zioJson)

  def docSources = T.sources {
    T.traverse(modules)(_.docSources)().flatten
  }

  def compileClasspath = T {
    T.traverse(modules)(_.compileClasspath)().flatten
  }

  def gitTags = T {
    os
      .proc("git", "tag", "-l", "v*.*.*")
      .call(VcsVersion.vcsBasePath)
      .out
      .trim()
      .split("\n")
      .reverse
  }

  def docVersions = T.source {
    val targetDir = T.dest / "_assets"

    val versions =
      gitTags()
        .filterNot(v => v.contains("-RC") || v.isBlank)
        .map(_.substring(1))

    def versionLink(version: String): String = {
      val splat = version.split("\\.")
      val (major, minor) = (splat(0).toInt, splat(1).toInt)
      if(major >= 2 && minor >= 2) s"https://www.javadoc.io/doc/io.github.iltotore/iron-docs_3/$version/docs/index.html"
      else s"https://www.javadoc.io/doc/io.github.iltotore/iron_3/$version/docs/index.html"
    }

    val links = versions.map(v => (v, ujson.Str(versionLink(v))))
    val withNightly = links :+ ("Nightly", ujson.Str("https://iltotore.github.io/iron/docs/index.html"))
    val json = ujson.Obj("versions" -> ujson.Obj.from(withNightly))

    val versionsFile = targetDir / "versions.json"
    os.write.over(versionsFile, ujson.write(json), createFolders = true)

    T.dest
  }

  def docResources = T.sources(millSourcePath, docVersions().path)

  def docRevision = T {
    val version = main.publishVersion()
    if(gitTags().contains(version)) version
    else "main"
  }

  def externalMappings = Seq(
    ".*cats.*" -> ("scaladoc3", "https://javadoc.io/doc/org.typelevel/cats-docs_3/latest/"),
    ".*io.circe.*" -> ("scaladoc2", "https://circe.github.io/circe/api/"),
    ".*ciris.*" -> ("scaladoc2", "https://cir.is/api/"),
    ".*chimney.*" -> ("scaladoc3", "https://javadoc.io/doc/io.scalaland/chimney_3/latest/"),
    ".*com.monovore.decline.*" -> ("scaladoc3", "https://javadoc.io/doc/com.monovore/decline_3/latest/"),
    ".*doobie.*" -> ("scaladoc3", "https://www.javadoc.io/doc/org.tpolecat/doobie-core_3/latest/"),
    ".*com.github.plokhotnyuk.jsoniter_scala.core.*" -> ("scaladoc3", "https://www.javadoc.io/doc/com.github.plokhotnyuk.jsoniter-scala/jsoniter-scala-core_3/latest/"),
    ".*pureconfig.*" -> ("scaladoc3", "https://www.javadoc.io/doc/com.github.pureconfig/pureconfig-core_3/latest/index.html"),
    ".*io.bullet.borer.*" -> ("scaladoc3", "https://javadoc.io/doc/io.bullet/borer-core_3/latest/"),
    ".*org.scalacheck.*" -> ("scaladoc3", "https://javadoc.io/doc/org.scalacheck/scalacheck_3/latest/"),
    ".*org.scodec.*" -> ("scaladoc3", "https://javadoc.io/doc/org.scodec/scodec-core_3/latest/"),
    ".*skunk.*" -> ("scaladoc3", "https://javadoc.io/doc/org.tpolecat/skunk-docs_3/latest/"),
    ".*upickle.core.*" -> ("scaladoc3", "https://javadoc.io/doc/com.lihaoyi/upickle-core_3/latest/"),
    ".*upickle[^\\.core].*" -> ("scaladoc3", "https://javadoc.io/doc/com.lihaoyi/upickle_3/latest/"),
    ".*zio.json.*" -> ("scaladoc3", "https://javadoc.io/doc/dev.zio/zio-json_3/latest/"),
    ".*zio.prelude.*" -> ("scaladoc3", "https://javadoc.io/doc/dev.zio/zio-prelude-docs_3/latest/"),
    ".*zio[^\\.json].*" -> ("scaladoc3", "https://javadoc.io/doc/dev.zio/zio_3/latest/")
  )

  def scalaDocOptions = {
    val externalMappingsFlag =
      externalMappings
        .map {
          case (regex, (docType, link)) => s"$regex::$docType::$link"
        }
        .mkString(",")


    Seq(
      "-project", "Iron",
      "-project-version", main.publishVersion(),
      "-versions-dictionary-url", "https://iltotore.github.io/iron/versions.json",
      "-source-links:github://Iltotore/iron",
      "-revision", docRevision(),
      "-snippet-compiler:nocompile",
      s"-social-links:github::${main.pomSettings().url}",
      s"-external-mappings:$externalMappingsFlag"
    )
  }
}

object main extends BaseModule {

  def artifactName = "iron"

  object test extends Tests

  object js extends JSCrossModule
  object native extends NativeCrossModule
  object native04 extends NativeCrossModule04
}

object examples extends Module {

  object catsValidation extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, cats)

    def ivyDeps = Agg(
      ivy"org.typelevel::cats-effect:3.3.14"
    )
  }

  object formCats extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, cats, circe)

    val circeVersion = "0.14.3"
    val http4sVersion = "0.23.16"

    def ivyDeps = Agg(
      //ivy"org.typelevel::cats-core:2.8.0",
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
      ivy"dev.zio::zio-test:2.1.6",
      ivy"dev.zio::zio-json:0.7.39",
      ivy"dev.zio::zio-http:3.1.0"
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

  object borerSerialization extends ScalaModule with ScalafmtModule {

    def scalaVersion = versions.scala

    def moduleDeps = Seq(main, borer)

    def ivyDeps = Agg(
      ivy"io.bullet::borer-core::1.13.0",
      ivy"io.bullet::borer-derivation::1.13.0"
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

  trait NativeCrossModule04 extends super.NativeCrossModule04 {

    def transitiveIvyDeps = T { super.transitiveIvyDeps().filter(d => !(d.dep.module.name.value == "scala3-library")) }

    def moduleDeps = Seq(main.native04)
  }
}

object sandbox extends SubModule {

  def artifactName = "sandbox"
}

object cats extends SubModule {

  def artifactName = "iron-cats"

  def ivyDeps = Agg(
    ivy"org.typelevel::cats-core::2.13.0",
    ivy"org.typelevel::algebra::2.13.0"
  )

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"org.typelevel::kittens:3.4.0"
    )
  }

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object upickle extends SubModule {

  def artifactName = "iron-upickle"

  def ivyDeps = Agg(
    ivy"com.lihaoyi::upickle:3.1.3"
  )

  object test extends Tests {
  }

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object circe extends SubModule {

  def artifactName = "iron-circe"

  def ivyDeps = Agg(
    ivy"io.circe::circe-core::0.14.10"
  )

  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"io.circe::circe-core::0.14.10"
    )
  }

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object playJson extends SubModule {

  def artifactName = "iron-play-json"

  def ivyDeps = Agg(
    ivy"org.playframework::play-json::3.0.5"
  )

  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"org.playframework::play-json::3.0.5"
    )
  }

  object js extends JSCrossModule
}

object ciris extends SubModule {

  def artifactName = "iron-ciris"

  def ivyDeps = Agg(
    ivy"is.cir::ciris::3.1.0"
  )

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"is.cir::ciris::3.1.0"
    )
  }

  object js extends JSCrossModule

  object native extends NativeCrossModule04

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
    ivy"dev.zio::zio-json::0.7.14"
  )

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"dev.zio::zio-json::0.7.14"
    )
  }

  object js extends JSCrossModule
}

object jsoniter extends SubModule {

  def artifactName = "iron-jsoniter"

  val jsoniterVersion = "2.33.2"

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

object borer extends SubModule {

  def artifactName = "iron-borer"

  def ivyDeps = Agg(
    ivy"io.bullet::borer-core::1.13.0"
  )

  object js extends JSCrossModule

  object test extends Tests
}

object skunk extends SubModule {

  def artifactName = "iron-skunk"

  def ivyDeps = Agg(
    ivy"org.tpolecat::skunk-core::1.0.0-M12"
  )

  object test extends Tests {
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"org.tpolecat::skunk-core::0.6.5"
    )
  }

  object js extends JSCrossModule

  object native extends NativeCrossModule04

}

object scalacheck extends SubModule {

  def artifactName = "iron-scalacheck"

  def ivyDeps = Agg(
    ivy"org.scalacheck::scalacheck::1.17.0"
  )

  object js extends JSCrossModule

  object test extends Tests
}

object doobie extends SubModule {

  def artifactName = "iron-doobie"

  def ivyDeps = Agg(
    ivy"org.tpolecat::doobie-core::1.0.0-RC11"
  )

  object test extends Tests{
    def ivyDeps = Agg(
      ivy"com.lihaoyi::utest:0.8.1",
      ivy"org.tpolecat::doobie-core::1.0.0-RC11",

    )
  }
}

object decline extends SubModule {

  def artifactName = "iron-decline"

  def ivyDeps = Agg(
    ivy"com.monovore::decline::2.5.0"
  )

  object test extends Tests

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object pureconfig extends SubModule {

  def artifactName = "iron-pureconfig"

  def ivyDeps = Agg(
    ivy"com.github.pureconfig::pureconfig-core::0.17.7"
  )

  object test extends Tests
}

object scodec extends SubModule {

  def artifactName = "iron-scodec"

  def ivyDeps = Agg(
    ivy"org.scodec::scodec-core::2.3.3"
  )

  object test extends Tests

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object chimney extends SubModule {

  def artifactName = "iron-chimney"

  def ivyDeps = Agg(
    ivy"io.scalaland::chimney::1.8.2"
  )

  object test extends Tests

  object js extends JSCrossModule

  object native extends NativeCrossModule
}

object dynosaur extends SubModule {

  def artifactName = "iron-dynosaur"

  def ivyDeps = Agg(
    ivy"org.systemfw::dynosaur-core:0.7.1"
  )

  object test extends Tests

  object js extends JSCrossModule
}
