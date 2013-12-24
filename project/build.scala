import sbt._
import Keys._

object BuildSettings {
    val buildOrganization = "org.analogweb"
    val buildVersion      = "0.1.0-SNAPSHOT"
    val buildScalaVersion = "2.10.2"

    val buildSettings = Defaults.defaultSettings ++ Seq (
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion
    )
}
object Dependencies {
  val core = "org.analogweb" % "analogweb-core" % "0.8.2-SNAPSHOT"
  val scalatest = "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
  val junit = "com.novocode" % "junit-interface" % "0.9" % "test"
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
        scalatest,
        junit
      ),
      artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
            artifact.name + "-" + module.revision + "." + artifact.extension
}
    )
  )
}
