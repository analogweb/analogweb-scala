Analogweb Framework Scala DSL
===============================================

[![Build Status](https://travis-ci.org/analogweb/scala-plugin.svg?branch=master)](https://travis-ci.org/analogweb/scala-plugin)

Analogweb is a tiny HTTP orientied framework.

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
  def hello = get("/hello") { request => 
    s"Hello, ${request.query("name")} !"
  }
}
```
