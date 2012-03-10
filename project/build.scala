import sbt._
import sbt.Keys._

object MyBuild extends Build {

  val groupName = "unfiltered-scalate-servlet"

  def id(name: String) = "%s-%s" format(groupName, name)

  override val settings = super.settings :+ 
    (shellPrompt := { s => Project.extract(s).currentProject.id + "> " })

  val defaultSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1",
    organization := "net.physalis",
    crossScalaVersions := Seq("2.9.0", "2.9.0-1", "2.9.1"),
    scalaVersion := "2.9.1",
    scalacOptions ++= Seq("-unchecked", "-deprecation")
  )

  object Dependency {

    val basic = {
      Seq(
        "org.scala-tools.time" %% "time" % "0.5"
      )
    }

    val io = {
      val version = "0.3.0"
      Seq(
        "com.github.scala-incubator.io" %% "scala-io-core" % version,
        "com.github.scala-incubator.io" %% "scala-io-file" % version
      )
    }

    val logging = Seq(
      "ch.qos.logback" % "logback-classic" % "0.9.25",
      "org.codehaus.groovy" % "groovy" % "1.8.0",
      "org.slf4j" % "slf4j-api" % "1.6.2",
      "org.clapper" %% "grizzled-slf4j" % "0.6.6"
    )

    val test = Seq(
      "org.scalatest" %% "scalatest" % "1.6.1",
      "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration"
    ).map { _ % "test" }

    val unfiltered = {
      val version = "0.6.1"
      Seq(
        "net.databinder" %% "unfiltered-filter" % version,
        "net.databinder" %% "unfiltered-jetty" % version,
	"org.fusesource.scalate" % "scalate-core" % "1.5.3"
      )
    }

    val jetty = Seq(
      "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"
    )

    val default = basic ++ io ++ logging ++ test ++ unfiltered
  }

  lazy val parent = Project(id("parent"), file(".")) aggregate(library, sampleWebApp)

  lazy val library = Project(id("library"), file("library"),
    settings = defaultSettings ++ Seq(
      libraryDependencies := Dependency.default,
      initialCommands := """
          |import scalax.io._
          |import scalax.file._
          |import org.scala_tools.time.Imports._
          |import unfiltered.request._
          |import unfiltered.response._
        """.stripMargin
    )
  )

  lazy val sampleWebApp = Project(id("web"), file("web"),
    settings = defaultSettings ++ Seq(
      libraryDependencies := Dependency.default ++ Dependency.jetty
    ) ++ com.github.siasia.WebPlugin.webSettings
  ) dependsOn(library)

}
