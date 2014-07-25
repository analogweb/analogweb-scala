package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb.ContainerAdaptor
import org.analogweb.InvocationMetadata
import org.analogweb.RequestContext
import org.analogweb.RequestValueResolvers
import org.analogweb.RequestValueResolver
import org.analogweb.TypeMapperContext
import org.analogweb.ResponseContext
import org.analogweb.RequestPathMetadata

@RunWith(classOf[JUnitRunner])
class ScalaInvocationFactorySpec extends Specification with Mockito {

  val factory = new ScalaInvocationFactory

  "ScalaInvocationMetadataFactory" should {
    "Foo contains invocation" in {
      val ca = mock[ContainerAdaptor]
      val im = mock[ScalaInvocationMetadata]
      val route = mock[Route]
      im.route returns route
      val rc = mock[RequestContext]
      val rsc = mock[ResponseContext]
      val tc = mock[TypeMapperContext]
      val rvr = mock[RequestValueResolvers]
      val actual = factory.createInvocation(ca, im, rc, rsc, tc, rvr)
      actual.isInstanceOf[ScalaInvocation] must beTrue
      actual.getInvocationInstance() === route
      actual.getInvocationArguments() === actual
      actual.getInvocationArguments().asList().isEmpty() must beTrue
    }
  }

}

