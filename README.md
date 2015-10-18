Analogweb Framework Scala DSL
===============================================

[![Build Status](https://travis-ci.org/analogweb/analogweb-scala.svg)](https://travis-ci.org/analogweb/analogweb-scala)
[![Coverage Status](https://coveralls.io/repos/analogweb/analogweb-scala/badge.svg)](https://coveralls.io/r/analogweb/analogweb-scala)
[![Codacy Badge](https://api.codacy.com/project/badge/bf94abcf981242debe9df3dbcd8d1764)](https://www.codacy.com/app/snowgoose-yk/analogweb-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-scala_2.11)
[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)

This plugin enables to execute Analogweb's route that was written in Scala.

Add to SBT dependency.

```scala
val scalaplugin = "org.analogweb" %% "analogweb-scala" % "0.9.10"
```
## Example

Write such code

```scala
import org.analogweb.core.Servers
import org.analogweb.scala.Analogweb

object App extends Analogweb {

  def main(args: Array[String]) = Servers.run
  
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
