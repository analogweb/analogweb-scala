package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ScalaInvocationMetadataFactorySpec extends Specification {

  val factory = new ScalaInvocationMetadataFactory

  "ScalaInvocationMetadataFactory" should {
    "Foo contains invocation" in {
      val actual = factory.containsInvocationClass(classOf[Foo])
      actual must beTrue
    }
    "Baa NOT contains invocation" in {
      val actual = factory.containsInvocationClass(classOf[Baa])
      actual must beFalse
    }
    "Create InvocationMetadata successful" in {
      val actual = factory.createInvocationMetadatas(classOf[Foo]).iterator().next()
      actual.getMethodName() === "foo"
      actual.getArgumentTypes().isEmpty must beTrue
      actual.getInvocationClass() === classOf[Foo]
      actual.getDefinedPath().getActualPath() === "/foo"
      actual.getDefinedPath().getRequestMethods().get(0) === "GET"
    }
    "Create InvocationMetadata NOT work" in {
      val actual = factory.createInvocationMetadatas(classOf[Baa])
      actual.isEmpty() must beTrue
    }
  }

}

class Foo extends Analogweb {
  def foo = get("/foo") { r =>
    "Foo"
  }
}

class Baa {
  def foo = { r: Request =>
    "Foo"
  }
}

