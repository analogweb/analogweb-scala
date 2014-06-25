import sbt._
import Keys._

object BuildSettings {
    val buildOrganization = "org.analogweb"
    val buildVersion      = "0.1.0-SNAPSHOT"
    val buildScalaVersion = "2.10.2"

    val buildSettings = Defaults.defaultSettings ++ Seq (
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      crossScalaVersions := Seq("2.10.2", "2.11.0")
    )
}
object Dependencies {
  val core = "org.analogweb" % "analogweb-core" % "0.8.2-SNAPSHOT"
  val jackson = "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.1.2"
  val junit = "com.novocode" % "junit-interface" % "0.9" % "test"
  val specs2 =  "org.specs2" % "specs2_2.11" % "2.3.12" % "test"
}

object Resolvers {
  val m2local = Resolver.mavenLocal 
  val all = Seq (
    m2local
  )
}

object AnalogwebScala extends Build {
  import BuildSettings._
  import Dependencies._
 
  lazy val root = Project (
    id = "analogweb-scala",
    base = file("."),
    settings = buildSettings ++ Seq (
      resolvers ++= Resolvers.all,
      libraryDependencies ++= Seq (
        core,
        jackson,
        junit,
        specs2
      ),
      artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
            artifact.name + "-" + module.revision + "." + artifact.extension
}
    )
  )
}
