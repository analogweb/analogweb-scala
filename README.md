Analogweb Framework Scala
===============================================

[![Build Status](https://travis-ci.org/analogweb/scala-plugin.svg)](https://travis-ci.org/analogweb/scala-plugin)
[![Coverage Status](https://coveralls.io/repos/analogweb/scala-plugin/badge.svg)](https://coveralls.io/r/analogweb/scala-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

This plugin enables to execute Analogweb's route that was written in Scala.

Add to SBT dependency.

```scala
val scalaplugin = "org.analogweb" %% "analogweb-scala" % "0.9.5-SNAPSHOT"
```
## Example

Write such code as below

```scala
import org.analogweb.core.Servers
import org.analogweb.scala.Analogweb

object Run {
  def main(args: Array[String]) = {
    Servers.create("http://localhost:8080").start
  }
  
  get("/ping") {
    "PONG"
  }
}
```

and you will get them.

```

$ curl http://localhost:8080/ping
PONG

```
