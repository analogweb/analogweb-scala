package org.analogweb.scala

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.analogweb.scala.Responses._

@RunWith(classOf[JUnitRunner])
class AnalogwebSpec extends Specification {

  val analogweb = new AnalogwebSpecFoo

  "Valid Get Route" in {
    val actual = analogweb.routes(0)
    actual.rawPath must_== "/foo"
    actual.method must_== "GET"
  }
  "Valid Post Route" in {
    val actual = analogweb.routes(1)
    actual.rawPath must_== "/foo"
    actual.method must_== "POST"
  }
  "Valid Put Route" in {
    val actual = analogweb.routes(2)
    actual.rawPath must_== "/baa"
    actual.method must_== "PUT"
  }
  "Valid Delete Route" in {
    val actual = analogweb.routes(3)
    actual.rawPath must_== "/baz"
    actual.method must_== "DELETE"
  }

}

class AnalogwebSpecFoo extends Analogweb {
  get("/foo") { r =>
    "Foo"
  }
  post("/foo") { r =>
    "Foo"
  }
  put("/baa") { r =>
    Forbidden
  }
  delete("/baz") { r =>
    Ok
  }
}

