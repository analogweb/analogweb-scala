package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.specs2.specification.BeforeExample
import org.analogweb._

@RunWith(classOf[JUnitRunner])
class RequestSpec extends Specification with Mockito with BeforeExample {

  val factory = new ScalaInvocationFactory

  var rc = mock[RequestContext]
  var rvr = mock[RequestValueResolvers]
  var im = mock[ScalaInvocationMetadata]
  var tc = mock[TypeMapperContext]
  var qp = mock[Parameters]
  var rh = mock[Headers]
  var request = new Request(rc, rvr, im, tc)

  def before = {

    rc = mock[RequestContext]
    rvr = mock[RequestValueResolvers]
    im = mock[ScalaInvocationMetadata]
    tc = mock[TypeMapperContext]
    qp = mock[Parameters]
    rh = mock[Headers]
    request = new Request(rc, rvr, im, tc)

  }

  "Request" should {
    "Returns avairable query parameter" in {
      rc.getQueryParameters returns qp
      qp.getValues("foo") returns java.util.Collections.emptyList()
      qp.getValues("baa") returns java.util.Arrays.asList("baz")
      val none = request.query("foo")
      none === None
      val actual = request.query("baa")
      actual.get === "baz"
    }
    "Returns avairable request header" in {
      rc.getRequestHeaders returns rh
      rh.getValues("x-foo") returns java.util.Collections.emptyList()
      rh.getValues("x-baa") returns java.util.Arrays.asList("baz")
      val none = request.header("x-foo")
      none === None
      val actual = request.header("x-baa")
      actual.get === "baz"
    }
    "Returns content type" in {
      val mt = mock[MediaType]
      mt.toString returns "application/json"
      rc.getContentType returns mt
      val actual = request.content
      actual.get === "application/json"
    }
    "Content type not avairable" in {
      rc.getContentType returns null
      val actual = request.content
      actual === None
    }
    "Returns path" in {
      val rpm = mock[RequestPath]
      rc.getRequestPath returns rpm
      val actual = request.requestPath
      actual === rpm
    }
  }
}

