import mill._, scalalib._

object main extends ScalaModule {

  def scalaVersion = "3.0.0-M3"

  object test extends Tests {
    def ivyDeps = Agg(ivy"org.scalatest:scalatest_2.13:3.2.3") //Will fork when ScalaTest will release a Dotty-compatible version
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}