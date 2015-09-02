package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb.ContainerAdaptor

@RunWith(classOf[JUnitRunner])
class ScalaInvocationMetadataFactorySpec extends Specification with Mockito {

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
      val ca = mock[ContainerAdaptor]
      val actual = factory.createInvocationMetadatas(classOf[Foo], ca).iterator().next()
      actual.getMethodName() === "GET(/foo)"
      actual.getArgumentTypes().isEmpty must beTrue
      actual.getInvocationClass() === classOf[Foo]
      actual.getDefinedPath().getActualPath() === "/foo"
      actual.getDefinedPath().getRequestMethods().get(0) === "GET"
    }
    "Create InvocationMetadata NOT work" in {
      val ca = mock[ContainerAdaptor]
      val actual = factory.createInvocationMetadatas(classOf[Baa], ca)
      actual.isEmpty() must beTrue
    }
  }

}

class Foo extends Analogweb {
  get("/foo") { r =>
    "Foo"
  }
}

class Baa {
  def baa = { r: Request =>
    "Foo"
  }
}

