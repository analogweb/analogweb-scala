import sbt._
import Keys._
import sbtrelease._
import ReleaseStateTransformations._
import xerial.sbt.Sonatype._
import scoverage._

val analogwebV = "0.10.1"
val specs2V    = "4.0.2"
val circeV     = "0.8.0"
val json4sV    = "3.5.3"

val coreDependencies =
  Seq(
    "org.analogweb" % "analogweb-core" % analogwebV,
    "org.specs2"    %% "specs2-core"   % specs2V % "test",
    "org.specs2"    %% "specs2-mock"   % specs2V % "test",
    "org.specs2"    %% "specs2-junit"  % specs2V % "test"
  )

lazy val baseSettings =
  Seq(
    organization := "org.analogweb",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    scalaVersion := crossScalaVersions.value.head,
    startYear := Some(2014),
    isSnapshot := version.value.trim.endsWith("SNAPSHOT"),
    description := "Analogweb Framework is tiny, simple, and pluggable web framework.",
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xlint",
      "-Xfuture",
      "-Yrangepos",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-Ydelambdafy:method",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-target:jvm-1.8"
    ),
    scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import")),
    scalacOptions in (Compile, console) += "-Yrepl-class-based",
    scalacOptions in Test ++= Seq("-Yrangepos"),
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    fork in Test := true,
    libraryDependencies ++= coreDependencies
  )

lazy val publishSettings =
  Seq(
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("http://analogweb.github.io/")),
    publishArtifact in Test := false,
    publishMavenStyle := true,
    publishTo := {
      val nexus =
        "https://oss.sonatype.org/"
      if (version.value.trim
            .endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/analogweb/analogweb-scala"),
        "scm:git:git@github.com:analogweb/analogweb-scala.git"
      )),
    developers := List(
      Developer("y2k2mt", "y2k2mt", "y2.k2mt@gmail.com", url("https://github.com/y2k2mt"))
    ),
    releasePublishArtifactsAction := PgpKeys.publishSigned.value
  )

lazy val allSettings = baseSettings ++ publishSettings ++ ReleasePlugin.projectSettings ++ sonatypeSettings ++ ScoverageSbtPlugin.projectSettings

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val analogweb =
  project
    .in(file("."))
    .settings(allSettings: _*)
    .settings(noPublish: _*)
    .settings(
      moduleName := "analogweb",
      libraryDependencies ++= coreDependencies ++ Seq(
        "org.analogweb" % "analogweb-netty" % analogwebV
      )
    )
    .settings(
      initialCommands in console :=
        """
        |import analogweb._
        |import analogweb.circe._
      """.stripMargin
    )
    .aggregate(core, json4s, circe)
    .dependsOn(core, circe)

lazy val core = project
  .settings(moduleName := "analogweb-scala")
  .settings(allSettings)

lazy val json4s = project
  .settings(moduleName := "analogweb-json4s")
  .settings(allSettings)
  .settings(
    moduleName := "analogweb-json4s",
    libraryDependencies ++= coreDependencies ++ Seq(
      "org.json4s" %% "json4s-jackson" % json4sV
    )
  )
  .dependsOn(core)

lazy val circe = project
  .settings(moduleName := "analogweb-circe")
  .settings(allSettings)
  .settings(
    moduleName := "analogweb-circe",
    libraryDependencies ++= coreDependencies ++ Seq(
      "io.circe" %% "circe-core"    % circeV,
      "io.circe" %% "circe-jawn"    % circeV,
      "io.circe" %% "circe-generic" % circeV
    )
  )
  .dependsOn(core)
