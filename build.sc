import mill._, scalalib._

object main extends ScalaModule {

  def scalaVersion = "3.0.0-M3"

  object test extends Tests {
    def ivyDeps = Agg(ivy"org.scalatest:::scalatest:3.2.3")
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}