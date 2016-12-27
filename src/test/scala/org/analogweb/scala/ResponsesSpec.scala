package org.analogweb.scala

import java.io.InputStream
import scala.xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core.response._
import org.analogweb.scala.Responses._

@RunWith(classOf[JUnitRunner])
class ResponsesSpec extends Specification with Mockito {

  val factory = new ScalaInvocationFactory
  trait mocks extends org.specs2.specification.Scope {
    val is = mock[InputStream]
  }

  "Responses" should {
    "Expected instance" in new mocks {
      val ok = Ok
      ok.getStatusCode === 200
      val redirect = RedirectTo("somewhere")
      redirect.isInstanceOf[Redirect] === true
      val okAsText = Ok(asText("foo"))
      okAsText.getStatusCode === 200
      okAsText.getRenderable.isInstanceOf[Text] === true
      val okAsResource = Ok(asResource(is, "foo"))
      okAsResource.getStatusCode === 200
      okAsResource.getRenderable.isInstanceOf[Resource] === true
      val statusObjAsHtml = Status(HttpStatus.OK, asHtml("foo"))
      statusObjAsHtml.getStatusCode === 200
      statusObjAsHtml.getRenderable.isInstanceOf[Html] === true
      val statusOfUnauthorized = Status(Unauthorized)
      statusOfUnauthorized.getStatusCode === 401
      val badRequest = BadRequest
      badRequest.getStatusCode === 400
      val notfound = NotFound
      notfound.getStatusCode === 404
      val forbiddenAsXml = Forbidden(asXml("a"))
      forbiddenAsXml.getStatusCode === 403
      forbiddenAsXml.getRenderable.isInstanceOf[Xml] === true
      val forbidden = Forbidden
      forbidden.getStatusCode === 403
      val internalServerErrorAsXml = InternalServerError(asXml("a"))
      internalServerErrorAsXml.getStatusCode === 500
      internalServerErrorAsXml.getRenderable.isInstanceOf[Xml] === true
      val internalServerError = InternalServerError
      internalServerError.getStatusCode === 500
      val notImplemented = Status(501)
      notImplemented.getStatusCode === 501
    }
  }

}
