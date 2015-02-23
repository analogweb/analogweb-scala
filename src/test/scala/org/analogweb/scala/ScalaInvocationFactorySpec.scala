package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb.ContainerAdaptor
import org.analogweb.InvocationMetadata
import org.analogweb.Invocation
import org.analogweb.RequestContext
import org.analogweb.RequestValueResolvers
import org.analogweb.RequestValueResolver
import org.analogweb.TypeMapperContext
import org.analogweb.ResponseContext
import org.analogweb.RequestPathMetadata
import org.analogweb.core.DefaultInvocation

@RunWith(classOf[JUnitRunner])
class ScalaInvocationFactorySpec extends Specification with Mockito {

  val factory = new ScalaInvocationFactory

  "ScalaInvocationMetadataFactory" should {
    "Foo contains scala invocation" in {
      val ca = mock[ContainerAdaptor]
      val im = mock[ScalaInvocationMetadata]
      val route = mock[Route]
      im.route returns route
      val invocationResult = None
      route.invoke(any[Request]) returns invocationResult 
      val rc = mock[RequestContext]
      val rsc = mock[ResponseContext]
      val tc = mock[TypeMapperContext]
      val rvr = mock[RequestValueResolvers]
      val actual = factory.createInvocation(ca, im, rc, rsc, tc, rvr)
      actual.isInstanceOf[ScalaInvocation] must beTrue
      actual.getInvocationInstance() === route
      actual.getInvocationArguments() === actual
      actual.getInvocationArguments().asList().isEmpty() must beTrue
      // nop
      actual.asInstanceOf[ScalaInvocation].replace(None) 
      // nop
      actual.asInstanceOf[ScalaInvocation].putInvocationArgument(1,None) 
      actual.invoke() === invocationResult 
    }
    "Foo not contains java invocation" in {
      val ca = mock[ContainerAdaptor]
      val im = mock[InvocationMetadata]
      im.getInvocationClass.asInstanceOf[Class[AnyRef]] returns classOf[ScalaInvocationFactorySpec].asInstanceOf[Class[AnyRef]]
      val rc = mock[RequestContext]
      val rsc = mock[ResponseContext]
      val tc = mock[TypeMapperContext]
      val rvr = mock[RequestValueResolvers]
      val actual = factory.createInvocation(ca, im, rc, rsc, tc, rvr)
      actual.isInstanceOf[DefaultInvocation] must beTrue
    }
  }

}

