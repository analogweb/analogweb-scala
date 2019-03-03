package org.analogweb.scala

import java.io.InputStream
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb.core.response._

@RunWith(classOf[JUnitRunner])
class ResponsesSpec extends Specification with Mockito {

  val factory = new ScalaInvocationFactory
  trait mocks extends org.specs2.specification.Scope {
    val is = mock[InputStream]
  }

  "Responses" should {
    "Expected instance" in new mocks with Responses {
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
      val createdAsText = Created(asText("foo"))
      createdAsText.getStatusCode === 201
      createdAsText.getRenderable.isInstanceOf[Text] === true
      val acceptedAsText = Accepted(asText("foo"))
      acceptedAsText.getStatusCode === 202
      acceptedAsText.getRenderable.isInstanceOf[Text] === true
      val noContent = NoContent
      noContent.getStatusCode === 204

      val statusObjAsHtml = Status(HttpStatus.OK, asHtml("foo"))
      statusObjAsHtml.getStatusCode === 200
      statusObjAsHtml.getRenderable.isInstanceOf[Html] === true
      val statusOfUnauthorized = Status(Unauthorized)
      statusOfUnauthorized.getStatusCode === 401

      val badRequest = BadRequest(asText("foo"))
      badRequest.getStatusCode === 400
      badRequest.getRenderable.isInstanceOf[Text] === true
      val forbidden = Forbidden(asText("foo"))
      forbidden.getStatusCode === 403
      forbidden.getRenderable.isInstanceOf[Text] === true
      val notfound = NotFound(asText("foo"))
      notfound.getStatusCode === 404
      notfound.getRenderable.isInstanceOf[Text] === true
      val methodNotAllowed = MethodNotAllowed(asText("foo"))
      methodNotAllowed.getStatusCode === 405
      methodNotAllowed.getRenderable.isInstanceOf[Text] === true
      val notAcceptable = NotAcceptable(asText("foo"))
      notAcceptable.getStatusCode === 406
      notAcceptable.getRenderable.isInstanceOf[Text] === true
      val conflict = Conflict(asText("foo"))
      conflict.getStatusCode === 409
      conflict.getRenderable.isInstanceOf[Text] === true
      val preconditionFailed = PreconditionFailed(asText("foo"))
      preconditionFailed.getStatusCode === 412
      preconditionFailed.getRenderable.isInstanceOf[Text] === true
      val unsupportedMediaType = UnsupportedMediaType(asText("foo"))
      unsupportedMediaType.getStatusCode === 415
      unsupportedMediaType.getRenderable.isInstanceOf[Text] === true

      val internalServerError = InternalServerError(asText("foo"))
      internalServerError.getStatusCode === 500
      internalServerError.getRenderable.isInstanceOf[Text] === true
      val notImplemented = Status(501)
      notImplemented.getStatusCode === 501
      val badGateway = BadGateway(asText("foo"))
      badGateway.getStatusCode === 502
      badGateway.getRenderable.isInstanceOf[Text] === true
      val serviceUnavailable = ServiceUnavailable(asText("foo"))
      serviceUnavailable.getStatusCode === 503
      serviceUnavailable.getRenderable.isInstanceOf[Text] === true
    }
  }

}
