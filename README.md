Analogweb Framework Scala
===============================================

[![Build Status](https://travis-ci.org/analogweb/analogweb-scala.svg)](https://travis-ci.org/analogweb/analogweb-scala)
[![codecov](https://codecov.io/gh/analogweb/analogweb-scala/branch/0.9.13/graph/badge.svg)](https://codecov.io/gh/analogweb/analogweb-scala)
[![Codacy Badge](https://api.codacy.com/project/badge/bf94abcf981242debe9df3dbcd8d1764)](https://www.codacy.com/app/snowgoose-yk/analogweb-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

## Quick Start

Create build.sbt

```scala
scalaVersion := "2.12.1" 
libraryDependencies ++= Seq (
  "org.analogweb" %% "analogweb-scala" % "0.10.1",
//  "org.analogweb"  % "analogweb-netty" % "0.10.1"
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
    |   get("ping") { r =>
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
