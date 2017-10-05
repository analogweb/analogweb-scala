//resolvers += "spray repo" at "http://repo.spray.io"

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.1")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.2.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
