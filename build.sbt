import sbt._
import Keys._
import sbtrelease._
import ReleaseStateTransformations._
import xerial.sbt.Sonatype._
import scoverage._

lazy val baseSettings = Seq(
  organization := "org.analogweb",
  version      := "0.9.13-SNAPSHOT",
  scalaVersion := "2.11.7",
  crossScalaVersions := Seq("2.10.5","2.11.7"),
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
              <id>y2k2mt</id>
              <name>y2k2mt</name>
              <url>https://github.com/y2k2mt</url>
          </developer>
      </developers>
  ),
  isSnapshot := true,
  scalacOptions ++= Seq("-feature","-deprecation", "-Yrangepos")
)
val additionalSettings = Defaults.defaultSettings ++ ReleasePlugin.projectSettings ++ sonatypeSettings ++ ScoverageSbtPlugin.projectSettings
val allResolvers = Seq (
 Resolver.mavenLocal, 
 Resolver.sonatypeRepo("snapshots"),
 "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)
val analogwebV = "0.9.12"
val jsonV      = "3.3.0"
val specs2V    = "3.7.2"
val allDependencies = Seq (
  "org.analogweb" % "analogweb-core" % analogwebV,
  "org.json4s" % "json4s-jackson" % jsonV cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  },
  "org.specs2" % "specs2-core" % specs2V % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  },
  "org.specs2" % "specs2-mock" % specs2V % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  },
  "org.specs2" % "specs2-junit" % specs2V % "test" cross CrossVersion.fullMapped {
      case "2.10.5" => "2.10"
      case "2.11.7" => "2.11"
  }
)
lazy val root = (project in file(".")).
  settings(baseSettings: _*).
  settings(additionalSettings: _*).
  settings(
    name := "analogweb-scala",
    resolvers ++= allResolvers,
    libraryDependencies ++= allDependencies 
  )
