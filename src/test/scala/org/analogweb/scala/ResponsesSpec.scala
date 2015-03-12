package org.analogweb.scala

import scala.xml.NodeSeq
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core.response._

@RunWith(classOf[JUnitRunner])
class ResponsesSpec extends Specification with Mockito {

  import org.analogweb.scala.Responses._

  val factory = new ScalaInvocationFactory

  "Responses" should {
    "Expected instance" in {
      val ok = Ok
      ok.getStatusCode === 200
      val redirect = RedirectTo("somewhere")
      redirect.isInstanceOf[Redirect] === true
      val okAsText = Ok(asText("foo"))
      okAsText.getStatusCode === 200
      okAsText.getRenderable.isInstanceOf[Text] === true
      val statusAsHtml = Status(asHtmlEntity(<h1>Hello</h1>), 200)
      statusAsHtml.getStatusCode === 200
      statusAsHtml.getRenderable.isInstanceOf[Html] === true
      val statusObjAsHtml = Status(asHtml("foo"), HttpStatus.OK)
      statusObjAsHtml.getStatusCode === 200
      statusObjAsHtml.getRenderable.isInstanceOf[Html] === true
      val badRequestAsJson = BadRequest(asJson("foo"))
      badRequestAsJson.getStatusCode === 400
      badRequestAsJson.getRenderable.isInstanceOf[Json] === true
      val badRequest = BadRequest
      badRequest.getStatusCode === 400
      val notFoundAsJson = NotFound(asJson(asJson("a")))
      notFoundAsJson.getStatusCode === 404
      notFoundAsJson.getRenderable.isInstanceOf[Json] === true
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

  "ScalaJsonFormatter" should {
    "be render" in {
      val formatter = new ScalaJsonFormatter

      val req = mock[RequestContext]
      val res = mock[ResponseContext]
      val c = new ScalaJsonFormatterA("snowgooseyk")
      val out = new java.io.ByteArrayOutputStream()
      formatter.formatAndWriteInto(req, res, "UTF-8", c).writeInto(out)
      new String(out.toByteArray()) === """{"id":"snowgooseyk"}"""
    }
  }

}

case class ScalaJsonFormatterA(id: String)
