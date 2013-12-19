Analog Web Framework Scala DSL
===============================================

AnalogWeb is a tiny HTTP orientied framework and it operated on Java 6+ and Scala 2+.

## Example

'''scala
import org.analogweb.scala.Analogweb

class Hello extends Analogweb {
    get("/") {
        "Hello, Analogweb Scala!"
    }
}

