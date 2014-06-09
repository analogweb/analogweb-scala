package org.analogweb.scala

import org.specs2.mutable._

class ScalaInvocationMetadataFactoryTest extends Specification {

  val factory = new ScalaInvocationMetadataFactory

  "ContainsCreateInvocationClass" should {
    "be successfull" in {
      val actual = factory.containsInvocationClass(classOf[Foo])
      actual must beTrue
    }
  }

}

class Foo extends Analogweb {
  def foo = get("/foo") { r =>
    "Foo"
  }
}

