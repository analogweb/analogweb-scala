package org.analogweb.scala

import org.specs2.mutable._

class AnalogwebSpec extends Specification {

  val analogweb = new AnalogwebSpecFoo

  "GetInvocation" should {
    "must be Route" in {
      analogweb.foo.isInstanceOf[Route] must_== true
    }
  }

}

class AnalogwebSpecFoo extends Analogweb {
  def foo = get("/foo") { r =>
    "Foo"
  }
}

