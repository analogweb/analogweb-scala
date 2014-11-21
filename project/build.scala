import sbt._
import Keys._
import sbtrelease._
import ReleaseStateTransformations._
import xerial.sbt.Sonatype._
import scoverage.ScoverageSbtPlugin._

object BuildSettings {
    val buildOrganization = "org.analogweb"
    val buildVersion      = "0.9.1"
    val buildScalaVersion = "2.10.4"
    val clossBuildScalaVersion = Seq("2.10.4","2.11.4")

    val buildSettings = Defaults.defaultSettings ++ instrumentSettings ++ ReleasePlugin.releaseSettings ++ sonatypeSettings ++ Seq (
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
                    <id>snowgooseky</id>
                    <name>snowgooseyk</name>
                    <url>https://github.com/snowgooseyk</url>
                </developer>
            </developers>
      ),
      isSnapshot := true,
      scalacOptions ++= Seq("-feature","-deprecation")
    )
}
object Dependencies {
  val core = "org.analogweb" % "analogweb-core" % "0.9.1"
  val fileupload = "org.analogweb" % "analogweb-commons-fileupload" % "0.9.1"
  val jackson = "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.4.1" cross CrossVersion.fullMapped {
      case "2.10.4" => "2.10"
      case "2.11.4" => "2.11"
  }
  val junit = "com.novocode" % "junit-interface" % "0.9" % "test"
  val specs =  "org.specs2" % "specs2" % "2.3.12" % "test" cross CrossVersion.fullMapped {
      case "2.10.4" => "2.10"
      case "2.11.4" => "2.11"
  }
}

object Resolvers {
  val m2local = "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
  val sonatype = Resolver.sonatypeRepo("snapshots")
  val all = Seq (
    m2local,
    sonatype
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
          fileupload,
          jackson,
          junit,
          specs
      )
    )
  )
}
