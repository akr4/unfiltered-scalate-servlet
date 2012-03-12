import sbt._
import sbt.Keys._

object MyBuild extends Build {

  override val settings = super.settings :+ 
    (shellPrompt := { s => Project.extract(s).currentProject.id + "> " })

  val defaultSettings = Defaults.defaultSettings ++ Seq(
    version := "1.0",
    organization := "net.physalis",
    crossScalaVersions := Seq("2.9.0", "2.9.0-1", "2.9.1"),
    scalaVersion := "2.9.1",
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    publishTo <<= (version) { version: String =>
      val local = Path.userHome / "projects" / "akr4.github.com" / "mvn-repo"
      val path = local / (if (version.trim.endsWith("SNAPSHOT")) "snapshots" else "releases")
      Some(Resolver.file("Github Pages", path)(Patterns(true, Resolver.mavenStyleBasePattern)))
    },
    publishMavenStyle := true
  )

  object Dependency {

    val logging = Seq(
      "org.slf4j" % "slf4j-api" % "1.6.2",
      "org.clapper" %% "grizzled-slf4j" % "0.6.6"
    )

    val loggingImpl = Seq(
      "ch.qos.logback" % "logback-classic" % "0.9.25",
      "org.codehaus.groovy" % "groovy" % "1.8.0"
    )

    val test = Seq(
      "org.scalatest" %% "scalatest" % "1.6.1",
      "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration"
    ).map { _ % "test" }

    val unfiltered = Seq(
      "net.databinder" %% "unfiltered-filter" % "0.6.1",
      "org.fusesource.scalate" % "scalate-core" % "1.5.3",
      "javax.servlet" % "servlet-api" % "2.3" % "provided"
    )

    val jetty = Seq(
      "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"
    )

    val default = logging ++ test ++ unfiltered
  }

  lazy val library = {
    Project("unfiltered-scalate-servlet", file("library"),
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
  }

  lazy val sampleWebApp = {
    import Dependency._
    Project("unfiltered-scalate-servlet-sample-web", file("web"),
      settings = defaultSettings ++ Seq(
        libraryDependencies := default ++ loggingImpl ++ jetty
      ) ++ com.github.siasia.WebPlugin.webSettings
    ) dependsOn(library)
  }

}
