package org.analogweb.scala

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AnalogwebSpec extends Specification {

  val analogweb = new AnalogwebSpecFoo

  "must be Route" in {
    analogweb.foo.isInstanceOf[Route] must_== true
  }
  "Valid Get Route" in {
    val actual = analogweb.foo
    actual.rawPath must_== "/foo"
    actual.method must_== "GET"
  }
  "Valid Post Route" in {
    val actual = analogweb.postFoo
    actual.rawPath must_== "/foo"
    actual.method must_== "POST"
  }
  "Valid Put Route" in {
    val actual = analogweb.baa
    actual.rawPath must_== "/baa"
    actual.method must_== "PUT"
  }
  "Valid Delete Route" in {
    val actual = analogweb.baz
    actual.rawPath must_== "/baz"
    actual.method must_== "DELETE"
  }

}

class AnalogwebSpecFoo extends Analogweb {
  import org.analogweb.scala.Responses._
  def foo = get("/foo") { r =>
    "Foo"
  }
  def postFoo = post("/foo") { r =>
    "Foo"
  }
  def baa = put("/baa") { r =>
    Forbidden
  }
  def baz = delete("/baz") { r =>
    Ok
  }
}

