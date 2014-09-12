package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._

@RunWith(classOf[JUnitRunner])
class RequestSpec extends Specification with Mockito {

  val factory = new ScalaInvocationFactory

  class Components {

    lazy val rc = mock[RequestContext]
    lazy val rvr = mock[RequestValueResolvers]
    lazy val im = mock[ScalaInvocationMetadata]
    lazy val tc = mock[TypeMapperContext]
    lazy val qp = mock[Parameters]
    lazy val rh = mock[Headers]
    lazy val request = new Request(rc, rvr, im, tc)

  }

  "Request" should {
    "Returns avairable query parameter" in {
      val cp = new Components
      cp.rc.getQueryParameters returns cp.qp
      cp.qp.getValues("foo") returns java.util.Collections.emptyList()
      cp.qp.getValues("baa") returns java.util.Arrays.asList("baz")
      val none = cp.request.query("foo")
      none === None
      val actual = cp.request.query("baa")
      actual.get === "baz"
    }
    "Returns avairable request header" in {
      val cp = new Components
      cp.rc.getRequestHeaders returns cp.rh
      cp.rh.getValues("x-foo") returns java.util.Collections.emptyList()
      cp.rh.getValues("x-baa") returns java.util.Arrays.asList("baz")
      val none = cp.request.header("x-foo")
      none === None
      val actual = cp.request.header("x-baa")
      actual.get === "baz"
    }
    "Returns content type" in {
      val cp = new Components
      val mt = mock[MediaType]
      mt.toString returns "application/json"
      cp.rc.getContentType returns mt
      val actual = cp.request.content
      actual.get === "application/json"
    }
    "Content type not avairable" in {
      val cp = new Components
      cp.rc.getContentType returns null
      val actual = cp.request.content
      actual === None
    }
    "Returns path" in {
      val cp = new Components
      val rpm = mock[RequestPath]
      cp.rc.getRequestPath returns rpm
      val actual = cp.request.requestPath
      actual === rpm
    }
  }
}

