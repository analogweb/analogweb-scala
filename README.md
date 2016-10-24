Analogweb Framework Scala
===============================================

[![Build Status](https://travis-ci.org/analogweb/analogweb-scala.svg)](https://travis-ci.org/analogweb/analogweb-scala)
[![codecov](https://codecov.io/gh/analogweb/analogweb-scala/branch/0.9.13/graph/badge.svg)](https://codecov.io/gh/analogweb/analogweb-scala)
[![Codacy Badge](https://api.codacy.com/project/badge/bf94abcf981242debe9df3dbcd8d1764)](https://www.codacy.com/app/snowgoose-yk/analogweb-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

## Quick Start

Add to sbt dependency.

```scala
val scalaplugin = "org.analogweb" %% "analogweb-scala" % "0.9.14"
```

Write a code.

```scala
package org.analogweb.example
import org.analogweb.core.Servers
import org.analogweb.scala.Analogweb

object App extends Analogweb {

  def main(args: Array[String]) = Servers.run()
  
  get("/ping") {
    "PONG"
  }

}
```

Run and you can get them.

```
$ sbt run
...
$ curl http://localhost:8080/ping
PONG
```
