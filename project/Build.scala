import sbt._
import Keys._
import sbtrelease._
import ReleaseStateTransformations._
import xerial.sbt.Sonatype._
import scoverage._

object BuildSettings {
    val buildOrganization = "org.analogweb"
    val buildVersion      = "0.9.8"
    val buildScalaVersion = "2.11.6"
    val clossBuildScalaVersion = Seq("2.10.5","2.11.6")

    val buildSettings = Defaults.defaultSettings ++ ReleasePlugin.releaseSettings ++ sonatypeSettings ++ ScoverageSbtPlugin.projectSettings  ++ Seq (
      organization := buildOrganization,
      version      := buildVersion,
      scalaVersion := buildScalaVersion,
      crossScalaVersions := clossBuildScalaVersion,
      licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
      description := "Analogweb Framework is tiny, simple, and pluggable web framework.",
      publishMavenStyle := true,
      publishTo <<= version { (v: String) =>
          val nexus = "https://oss.sonatype.org/"
          if (v.trim.endsWith("SNAPSHOT"))
              Some("snapshots" at nexus + "content/repositories/snapshots")
          else
              Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      publishArtifact in Test := false,
      scmInfo := Some(ScmInfo(
              url("https://github.com/analogweb/scala-plugin"),
                  "scm:git:git@github.com:analogweb/scala-plugin.git"
      )),
      pomExtra := (
            <url>http://analogweb.org</url>
            <developers>
                <developer>
                    <id>snowgooseyk</id>
                    <name>snowgooseyk</name>
                    <url>https://github.com/snowgooseyk</url>
                </developer>
            </developers>
      ),
      isSnapshot := true,
      scalacOptions ++= Seq("-feature","-deprecation", "-Yrangepos")
    )
}
object Dependencies {
  val core   = "org.analogweb" % "analogweb-core" % "0.9.8"
  val json4s = "org.json4s" % "json4s-jackson" % "3.2.11" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.6" => "2.11"
  }
  val specs  = "org.specs2" % "specs2-core" % "3.6.1" % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.6" => "2.11"
  }
  val specsMock  = "org.specs2" % "specs2-mock" % "3.6.1" % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.6" => "2.11"
  }
  val specsJunit  = "org.specs2" % "specs2-junit" % "3.6.1" % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.6" => "2.11"
  }
  val all = Seq (
    core,
    json4s,
    specs,
    specsMock,
    specsJunit
  )
}

object Resolvers {
  val m2local = Resolver.mavenLocal 
  val sonatype = Resolver.sonatypeRepo("snapshots")
  val scalazBintray = "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
  val all = Seq (
    m2local,
    sonatype,
    scalazBintray
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
      libraryDependencies ++= Dependencies.all 
    )
  )
}
