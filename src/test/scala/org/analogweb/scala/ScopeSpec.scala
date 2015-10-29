package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core._
import java.lang.annotation.Annotation

@RunWith(classOf[JUnitRunner])
class ScopeSpec extends Specification with Mockito {

  class MockRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = if (key == "foo") "That's it" else null
  }
  class OtherRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = "That's it"
  }

  class SpecificRequestValueResolver extends SpecificMediaTypeRequestValueResolver {
    override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = "That's it"
    override final def supports(mediaType: MediaType) = mediaType.toString().startsWith("text/plain")
  }

  val mockResolver = classOf[MockRequestValueResolver]
  val otherResolver = classOf[OtherRequestValueResolver]
  val specificResolver = classOf[SpecificRequestValueResolver]

  trait mocks extends org.specs2.specification.Scope {
    val rc = mock[RequestContext]
    val rvr = mock[RequestValueResolvers]
    val im = mock[ScalaInvocationMetadata]
    val tc = mock[TypeMapperContext]
    val qp = mock[Parameters]
    val rh = mock[Headers]
    val request = new Request(rc, rvr, im, tc)
  }

  "Scope" should {
    "Returns avairable scope" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(mockResolver, request)
      actual.as[String]("foo") must beSome(===("That's it"))
    }
    "Returns avairable scope of" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(mockResolver, request)
      actual.of("foo") must beSome(===("That's it"))
    }
    "Returns not avairable scope of" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(mockResolver, request)
      actual.of("bar") must beNone
    }
    "Returns avairable scope via get" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(mockResolver, request)
      actual.get("foo") must_== "That's it"
    }
    "Returns not avairable scope via get" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(mockResolver, request)
      actual.get("bar") must_== ""
    }
    "Returns not avairable scope" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual = DefaultScope(otherResolver, request)
      actual.as[String]("foo") must throwA[IllegalArgumentException]
    }
    "Supports content types" in new mocks {
      rvr.findRequestValueResolver(specificResolver) returns new SpecificRequestValueResolver()
      rc.getContentType() returns MediaTypes.TEXT_PLAIN_TYPE
      val actual = DefaultScope(specificResolver, request)
      actual.as[String]("foo") must beSome(===("That's it"))
    }
    "Not supports content types" in new mocks {
      rvr.findRequestValueResolver(specificResolver) returns new SpecificRequestValueResolver()
      rc.getContentType() returns MediaTypes.APPLICATION_JSON_TYPE
      val actual = DefaultScope(specificResolver, request)
      actual.as[String]("foo") must throwA[UnsupportedMediaTypeException]
    }
  }
}

