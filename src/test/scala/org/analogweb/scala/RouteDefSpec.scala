package org.analogweb.scala

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RouteDefSpec extends Specification {

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
  "Valid Scope Routes" in {
    val actual = analogweb.routes(4)
    actual.rawPath must_== "/root/foo"
    actual.method must_== "GET"
    val actualPost = analogweb.routes(5)
    actualPost.rawPath must_== "/root/bar"
    actualPost.method must_== "POST"
    val actualGet = analogweb.routes(6)
    actualGet.rawPath must_== "/root/bar"
    actualGet.method must_== "GET"
  }

}

class AnalogwebSpecFoo extends LooseRouteDef {
  get("/foo") { r =>
    "Foo"
  }
  post("/foo") { r =>
    "Foo"
  }
  put("/baa") { r =>
    "Baa"
  }
  delete("/baz") { r =>
    "Baz"
  }
  scope("/root") {
    get("/foo") { r =>
      "Foo"
    } ~ post("/bar") { r =>
      "Bar"
    } ~ get("/bar") { r =>
      "Bar"
    }
  }
}

