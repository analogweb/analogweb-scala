package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import java.lang.annotation.Annotation

@RunWith(classOf[JUnitRunner])
class ScopeSpec extends Specification with Mockito {

  class MockRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = "That's it" 
  }
  class OtherRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = "That's it" 
  }

  val mockResolver = classOf[MockRequestValueResolver]
  val otherResolver = classOf[OtherRequestValueResolver]

  trait mocks extends org.specs2.specification.Scope {
    var rc = mock[RequestContext]
    var rvr = mock[RequestValueResolvers]
    var im = mock[ScalaInvocationMetadata]
    var tc = mock[TypeMapperContext]
    var qp = mock[Parameters]
    var rh = mock[Headers]
    var request = new Request(rc, rvr, im, tc)
  }

  "Scope" should {
    "Returns avairable scope" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(mockResolver, request)
      actual.as[String]("foo").get mustEqual "That's it"
    }
    "Returns not avairable scope" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(otherResolver, request)
      actual.as[String]("foo") must throwA[IllegalArgumentException]
    }
  }
}

