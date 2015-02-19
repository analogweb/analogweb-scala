Analogweb Framework Scala
===============================================

[![Build Status](https://travis-ci.org/analogweb/scala-plugin.svg?branch=0.9.3)](https://travis-ci.org/analogweb/scala-plugin)
[![Coverage Status](https://coveralls.io/repos/analogweb/scala-plugin/badge.svg?branch=0.9.3)](https://coveralls.io/r/analogweb/scala-plugin?branch=0.9.3)


This plugin enables to execute Analogweb's route written in Scala.

Add to SBT dependency.

```scala
val scalaplugin = "org.analogweb" %% "analogweb-scala" % "0.9.3-SNAPSHOT"
```
## Example

```scala
import org.analogweb.core.httpserver.HttpServers
import org.analogweb.scala.Analogweb
import java.net.URI

object Run {
  def main(args: Array[String]): Unit = {
    HttpServers.create(URI.create("http://localhost:8080")).start()
  }
}

class Hello extends Analogweb {
  get("/hello") { r => 
    s"Hello, ${r.query("name")} !"
  }
}
```
