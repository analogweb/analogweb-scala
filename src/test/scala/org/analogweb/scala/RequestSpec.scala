package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._

@RunWith(classOf[JUnitRunner])
class RequestSpec extends Specification with Mockito {

  val factory = new ScalaInvocationFactory

  trait mocks extends org.specs2.specification.Scope {
    var rc = mock[RequestContext]
    var rvr = mock[RequestValueResolvers]
    var im = mock[ScalaInvocationMetadata]
    var tc = mock[TypeMapperContext]
    var qp = mock[Parameters]
    var rh = mock[Headers]
    var request = new Request(rc, rvr, im, tc)
  }

  "Request" should {
    "Returns avairable query parameter" in new mocks {
      rc.getQueryParameters returns qp
      qp.getValues("foo") returns java.util.Collections.emptyList()
      qp.getValues("baa") returns java.util.Arrays.asList("baz")
      val none = request.queryOption("foo")
      none must beNone
      val actual = request.queryOption("baa")
      actual.get must beEqualTo("baz")
      val noOption = request.query("baa")
      noOption must beEqualTo("baz")
    }
    "Returns avairable request header" in new mocks {
      rc.getRequestHeaders returns rh
      rh.getValues("x-foo") returns java.util.Collections.emptyList()
      rh.getValues("x-baa") returns java.util.Arrays.asList("baz")
      val none = request.headerOption("x-foo")
      none must beNone
      val actual = request.headerOption("x-baa")
      actual.get must beEqualTo("baz")
      val noOption = request.header("x-baa")
      noOption must beEqualTo("baz")
    }
    "Returns content type" in new mocks {
      val mt = mock[MediaType]
      mt.toString returns "application/json"
      rc.getContentType returns mt
      val actual = request.content
      actual.get must beEqualTo("application/json")
    }
    "Content type not avairable" in new mocks {
      rc.getContentType returns null
      val actual = request.content
      actual must beNone
    }
    "Returns path" in new mocks {
      val rpm = mock[RequestPath]
      rc.getRequestPath returns rpm
      val actual = request.requestPath
      actual must beEqualTo(rpm)
    }
  }
}

