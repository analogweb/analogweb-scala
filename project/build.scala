import sbt._
import Keys._

object BuildSettings {
    val buildOrganization = "org.analogweb"
    val buildVersion      = "0.9.0-SNAPSHOT"
    val buildScalaVersion = "2.10.4"
    val clossBuildScalaVersion = Seq("2.10.4","2.11.0")

    val buildSettings = Defaults.defaultSettings ++ Seq (
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      crossScalaVersions := clossBuildScalaVersion
    )
}
object Dependencies {
  val core = "org.analogweb" % "analogweb-core" % "0.9.0-SNAPSHOT"
  val jackson = "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.4.1" cross CrossVersion.fullMapped {
      case "2.10.4" => "2.10"
      case "2.11.0" => "2.11"
  }
  val junit = "com.novocode" % "junit-interface" % "0.9" % "test"
  val specs =  "org.specs2" % "specs2" % "2.3.12" % "test" cross CrossVersion.fullMapped {
      case "2.10.4" => "2.10"
      case "2.11.0" => "2.11"
  }
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
      libraryDependencies ++= Seq(
          core,
          jackson,
          junit,
          specs
      ),
      artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
            artifact.name + "-" + module.revision + "." + artifact.extension
      }
    )
  )
}
