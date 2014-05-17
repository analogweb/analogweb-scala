package org.analogweb.scala

import org.analogweb.scala._

class Hello extends Analogweb {
    def hello = get("/hello") { r =>
        println(r)
      "Hello Scala!"
    }
}

