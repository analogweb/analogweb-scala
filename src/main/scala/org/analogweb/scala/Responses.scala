package org.analogweb.scala

import java.io.{ File, InputStream, OutputStream }
import scala.collection.mutable.Map
import scala.collection.convert.decorateAsJava._
import scala.xml.NodeSeq
import org.analogweb.{ Renderable, ResponseFormatter, RequestContext, ResponseContext }
import org.analogweb.ResponseEntity
import org.analogweb.core.response._
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization

trait Responses {
  def asText(obj: String) = Text.`with`(obj)
  def asHtmlEntity(obj: String) = Html.`with`(obj)
  def asHtmlEntity(obj: NodeSeq): Html = asHtmlEntity(obj.toString())
  def asHtml(templatePath: String) = Html.as(templatePath)
  def asHtml(templatePath: String, context: Map[String, AnyRef]) = Html.as(templatePath, context.asJava)
  def asJson(obj: AnyRef) = new ScalaJsonObject(obj)
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)
  def asXml(obj: AnyRef) = org.analogweb.core.response.Xml.as(obj)
  def asResource(stream: InputStream, filename: String) = Resource.as(stream, filename)
  def Status(statusCode: Int) = HttpStatus.valueOf(statusCode)
  def Status(status: HttpStatus): HttpStatus = Status(status.getStatusCode())
  def Status(responseBody: Renderable, statusCode: Int) = HttpStatus.valueOf(statusCode).`with`(responseBody)
  def Status(responseBody: Renderable, status: HttpStatus): HttpStatus = Status(responseBody, status.getStatusCode())
  def RedirectTo(url: String) = Redirect.to(url)
  def Ok(obj: Renderable) = HttpStatus.OK.`with`(obj)
  def Ok = HttpStatus.OK
  def BadRequest(obj: Renderable) = HttpStatus.BAD_REQUEST.`with`(obj)
  def BadRequest = HttpStatus.BAD_REQUEST
  def NotFound(obj: Renderable) = HttpStatus.NOT_FOUND.`with`(obj)
  def NotFound = HttpStatus.NOT_FOUND
  def Forbidden(obj: Renderable) = HttpStatus.FORBIDDEN.`with`(obj)
  def Forbidden = HttpStatus.FORBIDDEN
  def InternalServerError(obj: Renderable) = HttpStatus.INTERNAL_SERVER_ERROR.`with`(obj)
  def InternalServerError = HttpStatus.INTERNAL_SERVER_ERROR
}
object Responses extends Responses

class ScalaJsonObject(obj: AnyRef) extends Json(obj)
class ScalaJsonText(text: String) extends Json(text)

class ScalaJsonFormatter extends ResponseFormatter {

  implicit val formats = Serialization.formats(NoTypeHints)

  override def formatAndWriteInto(request: RequestContext, response: ResponseContext, charset: String,
    source: Any): ResponseEntity = {
    new ResponseEntity() {
      override def writeInto(responseBody: OutputStream) = {
        val s = source match {
          case v: JValue => JsonMethods.compact(JsonMethods.render(v))
          case _         => Serialization.write(source.asInstanceOf[AnyRef])
        }
        responseBody.write(s.getBytes(charset))
        responseBody.flush
      }
      override def getContentLength = -1
    }
  }

}
