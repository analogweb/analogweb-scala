Analogweb Framework Scala
===============================================

[![Build Status](https://travis-ci.org/analogweb/analogweb-scala.svg)](https://travis-ci.org/analogweb/analogweb-scala)
[![codecov](https://codecov.io/gh/analogweb/analogweb-scala/branch/master/graph/badge.svg)](https://codecov.io/gh/analogweb/analogweb-scala)
[![Codacy Badge](https://api.codacy.com/project/badge/bf94abcf981242debe9df3dbcd8d1764)](https://www.codacy.com/app/snowgoose-yk/analogweb-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fanalogweb%2Fanalogweb-scala.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fanalogweb%2Fanalogweb-scala?ref=badge_shield)

## Quick Start

Create build.sbt

```scala
scalaVersion := "2.12.8"
libraryDependencies ++= Seq (
  "org.analogweb" %% "analogweb-scala" % "0.11.0"
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
    |   get("ping") { _ =>
    |     "PONG"
    |   }
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
