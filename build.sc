import mill._, scalalib._, scalafmt._

object main extends ScalaModule with ScalafmtModule {

  def scalaVersion = "3.2.0-RC3"

//  def scalacOptions = super.scalacOptions() :+ "-explain"
}