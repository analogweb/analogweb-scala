package org.analogweb.scala

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AnalogwebSpec extends Specification {

  val analogweb = new AnalogwebSpecFoo

  "GetInvocation" should {
    "must be Route" in {
      analogweb.foo.isInstanceOf[Route] must_== true
    }
    "Valid Route" in {
      val actual = analogweb.foo
      actual.rawPath must_== "/foo"
      actual.method must_== "GET"
    }
  }

}

class AnalogwebSpecFoo extends Analogweb {
  def foo = get("/foo") { r =>
    "Foo"
  }
}

