import sbt._
import Keys._
import sbtrelease._
import ReleaseStateTransformations._
import xerial.sbt.Sonatype._
import scoverage._

lazy val baseSettings = Seq(
  organization := "org.analogweb",
  crossScalaVersions := Seq("2.11.8", "2.12.2"),
  scalaVersion := crossScalaVersions.value.head,
  licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
  description := "Analogweb Framework is tiny, simple, and pluggable web framework.",
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  scmInfo := Some(ScmInfo(
    url("https://github.com/analogweb/scala-plugin"),
    "scm:git:git@github.com:analogweb/scala-plugin.git"
  )),
  developers := List(
    Developer("y2k2mt", "y2k2mt", "y2_k2mt@gmail.com",url("https://github.com/y2k2mt"))
  ),
  startYear := Some(2014),
  isSnapshot := false,
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-unchecked",
    "-Xlint",
    "-Yrangepos",
    "-language:existentials",
    "-Ydelambdafy:method",
    "-target:jvm-1.8"
  ),
  releasePublishArtifactsAction := PgpKeys.publishSigned.value
)
val additionalSettings = ReleasePlugin.projectSettings ++ sonatypeSettings ++ ScoverageSbtPlugin.projectSettings
val allResolvers = Seq(
  Resolver.mavenLocal,
  Resolver.sonatypeRepo("snapshots"),
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)
val analogwebV = "0.10.0-SNAPSHOT"
val specs2V = "3.8.8"

val allDependencies = Seq(
  "org.analogweb" % "analogweb-core" % analogwebV,
  "org.specs2" %% "specs2-core" % specs2V % "test",
  "org.specs2" %% "specs2-mock" % specs2V % "test",
  "org.specs2" %% "specs2-junit" % specs2V % "test"
)
lazy val root = (project in file(".")).
  settings(baseSettings: _*).
  settings(additionalSettings: _*).
  settings(
    name := "analogweb-scala",
    resolvers ++= allResolvers,
    libraryDependencies ++= allDependencies
  )
