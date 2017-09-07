package org.analogweb.scala

import java.io.InputStream
import scala.collection.JavaConverters._
import scala.collection.mutable.Map
import org.analogweb._, core.DefaultReadableBuffer._, core.response._

trait Responses {
  def asText(obj: String) =
    Text.`with`(obj)
  def asHtmlEntity(obj: String) =
    Html.`with`(obj)
  def asHtml(templatePath: String): Html =
    asHtml(templatePath, Map.empty)
  def asHtml(templatePath: String, context: Map[String, AnyRef]): Html =
    Html
      .as(templatePath, context.asJava)
  def asXml(obj: AnyRef) =
    org.analogweb.core.response.Xml
      .as(obj)
  def asResource(stream: InputStream): Resource =
    asResource(stream, "").withoutContentDisposition
  def asResource(stream: InputStream, filename: String): Resource =
    asResource(readBuffer(stream), filename)
  def asResource(buffer: ReadableBuffer, filename: String = ""): Resource =
    Resource
      .as(buffer, filename)
  def Status(statusCode: Int) =
    HttpStatus
      .valueOf(statusCode)
  def Status(status: HttpStatus): HttpStatus =
    Status(
      status
        .getStatusCode())
  def Status(statusCode: Int, responseBody: Renderable) =
    HttpStatus
      .valueOf(statusCode)
      .`with`(responseBody)
  def Status(status: HttpStatus, responseBody: Renderable): HttpStatus =
    Status(status
             .getStatusCode(),
           responseBody)
  def RedirectTo(url: String) =
    Redirect
      .to(url)
  def Ok(responseBody: Renderable): HttpStatus =
    Ok.`with`(responseBody)
  def Ok: HttpStatus =
    HttpStatus.OK
  def Created(responseBody: Renderable): HttpStatus =
    Created
      .`with`(responseBody)
  def Created: HttpStatus =
    HttpStatus.CREATED
  def Accepted(responseBody: Renderable): HttpStatus =
    Accepted
      .`with`(responseBody)
  def Accepted: HttpStatus =
    HttpStatus.ACCEPTED
  def NoContent: HttpStatus =
    HttpStatus.NO_CONTENT
  def MovedPermanently: HttpStatus =
    HttpStatus.MOVED_PERMANENTLY
  def Found: HttpStatus =
    HttpStatus.FOUND
  def SeeOther: HttpStatus =
    HttpStatus.SEE_OTHER
  def NotModified: HttpStatus =
    HttpStatus.NOT_MODIFIED
  def BadRequest(obj: Renderable): HttpStatus =
    BadRequest.`with`(obj)
  def BadRequest: HttpStatus =
    HttpStatus.BAD_REQUEST
  def Unauthorized(obj: Renderable): HttpStatus =
    Unauthorized.`with`(obj)
  def Unauthorized: HttpStatus =
    HttpStatus.UNAUTHORIZED
  def Forbidden(obj: Renderable): HttpStatus =
    Forbidden.`with`(obj)
  def Forbidden: HttpStatus =
    HttpStatus.FORBIDDEN
  def NotFound(obj: Renderable): HttpStatus =
    NotFound.`with`(obj)
  def NotFound: HttpStatus =
    HttpStatus.NOT_FOUND
  def MethodNotAllowed(obj: Renderable): HttpStatus =
    MethodNotAllowed.`with`(obj)
  def MethodNotAllowed: HttpStatus =
    HttpStatus.METHOD_NOT_ALLOWED
  def NotAcceptable(obj: Renderable): HttpStatus =
    NotAcceptable.`with`(obj)
  def NotAcceptable: HttpStatus =
    HttpStatus.NOT_ACCEPTABLE
  def Conflict(obj: Renderable): HttpStatus =
    Conflict.`with`(obj)
  def Conflict: HttpStatus =
    HttpStatus.CONFLICT
  def PreconditionFailed(obj: Renderable): HttpStatus =
    PreconditionFailed.`with`(obj)
  def PreconditionFailed: HttpStatus =
    HttpStatus.PRECONDITION_FAILED
  def UnsupportedMediaType(obj: Renderable): HttpStatus =
    UnsupportedMediaType.`with`(obj)
  def UnsupportedMediaType: HttpStatus =
    HttpStatus.UNSUPPORTED_MEDIA_TYPE
  def InternalServerError(obj: Renderable): HttpStatus =
    InternalServerError.`with`(obj)
  def InternalServerError: HttpStatus =
    HttpStatus.INTERNAL_SERVER_ERROR
  def BadGateway(obj: Renderable): HttpStatus =
    BadGateway.`with`(obj)
  def BadGateway: HttpStatus =
    HttpStatus.BAD_GATEWAY
  def ServiceUnavailable(obj: Renderable): HttpStatus =
    ServiceUnavailable.`with`(obj)
  def ServiceUnavailable: HttpStatus =
    HttpStatus.SERVICE_UNAVAILABLE
}
object Responses extends Responses

class ScalaJsonObject(obj: AnyRef) extends Json(obj)

class ScalaJsonText(text: String) extends Json(text)
