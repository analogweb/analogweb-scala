Analogweb Framework Scala DSL
===============================================

Analogweb is a tiny HTTP orientied framework.
This DSL operated on Scala 2+.

## Example

```scala
import org.analogweb.scala.Analogweb

class Hello extends Analogweb {
    def hello = get("/hello") { request => 
        s"Hello, ${request parameter "name"} !"
    }
}
```
