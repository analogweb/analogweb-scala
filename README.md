Analogweb Framework Scala
===============================================

[![Build Status](https://travis-ci.org/analogweb/analogweb-scala.svg)](https://travis-ci.org/analogweb/analogweb-scala)
[![codecov](https://codecov.io/gh/analogweb/analogweb-scala/branch/master/graph/badge.svg)](https://codecov.io/gh/analogweb/analogweb-scala)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/7a112498f9ae4e3d996a8a74d59a1c4e)](https://www.codacy.com/manual/y2k2mt/analogweb-scala?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=analogweb/analogweb-scala&amp;utm_campaign=Badge_Grade)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.13)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fanalogweb%2Fanalogweb-scala.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fanalogweb%2Fanalogweb-scala?ref=badge_shield)

## Quick Start

Create build.sbt

```scala
scalaVersion := "3.1.1"
libraryDependencies ++= Seq (
  "org.analogweb" %% "analogweb-scala" % "0.13.0"
)
```

Start sbt console.

```
$ sbt console
```

Write a code.

```scala
scala> import analogweb._
import analogweb._

scala> http("localhost",8000) {
    |   get("/ping") { "PONG" }
    | }.run
...
INFO: An Analogweb application has been booted. (Erapsed time: 412ms)
```

and you will get them.

```
$ curl localhost:8000/ping
PONG
```


## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fanalogweb%2Fanalogweb-scala.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fanalogweb%2Fanalogweb-scala?ref=badge_large)
