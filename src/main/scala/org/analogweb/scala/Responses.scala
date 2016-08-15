package org.analogweb.scala

import java.io.{ File, InputStream, OutputStream, FileInputStream, ByteArrayInputStream }
import scala.collection.mutable.Map
import scala.collection.convert.decorateAsJava._
import scala.concurrent.Future
import scala.xml.NodeSeq
import org.analogweb.{ Renderable, ResponseFormatter, RequestContext, ResponseContext, ResponseEntity }
import org.analogweb.core.response._
import org.analogweb.util.IOUtils
import org.json4s._
import org.json4s.jackson.{ JsonMethods, Serialization }

trait Responses {
  def asText(obj: String) = Text.`with`(obj)
  def asHtmlEntity(obj: String) = Html.`with`(obj)
  @deprecated("Nothing to alternative.", "0.9.13")
  def asHtmlEntity(obj: NodeSeq): Html = asHtmlEntity(obj.toString())
  def asHtml(templatePath: String) = Html.as(templatePath)
  def asHtml(templatePath: String, context: Map[String, AnyRef]) = Html.as(templatePath, context.asJava)
  def asJson(obj: AnyRef)(implicit formats: Formats = Serialization.formats(NoTypeHints)) = new ScalaJsonObject((obj, formats))
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)
  def asXml(obj: AnyRef) = org.analogweb.core.response.Xml.as(obj)
  def asResource(stream: InputStream) = Resource.as(stream, "").withoutContentDisposition
  def asResource(stream: InputStream, filename: String) = Resource.as(stream, filename)
  def Status(statusCode: Int) = HttpStatus.valueOf(statusCode)
  def Status(status: HttpStatus): HttpStatus = Status(status.getStatusCode())
  @deprecated("Use Status(statusCode,responseBody) instead.", "0.9.12")
  def Status(responseBody: Renderable, statusCode: Int) = HttpStatus.valueOf(statusCode).`with`(responseBody)
  def Status(statusCode: Int, responseBody: Renderable) = HttpStatus.valueOf(statusCode).`with`(responseBody)
  @deprecated("Use Status(status,responseBody) instead.", "0.9.12")
  def Status(responseBody: Renderable, status: HttpStatus): HttpStatus = Status(status.getStatusCode(), responseBody)
  def Status(status: HttpStatus, responseBody: Renderable): HttpStatus = Status(status.getStatusCode(), responseBody)
  def RedirectTo(url: String) = Redirect.to(url)
  def Ok(responseBody: Renderable) = HttpStatus.OK.`with`(responseBody)
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

  val defaultFormats = Serialization.formats(NoTypeHints)

  override def formatAndWriteInto(request: RequestContext, response: ResponseContext, charset: String,
                                  source: Any): ResponseEntity = {
    new ResponseEntity() {
      lazy val contents: (InputStream, Int) = jsonContents

      def jsonContents = {
        source match {
          case f: FileInputStream      => (f, f.available())
          case b: ByteArrayInputStream => (b, b.available())
          case i: InputStream          => (i, -1)
          case _ => {
            val bytes = toBytes
            (new ByteArrayInputStream(bytes), bytes.length)
          }
        }
      }

      def toBytes = {
        val serialized = source match {
          case (obj, formats) => formats match {
            case f: Formats => Serialization.write(obj.asInstanceOf[AnyRef])(f)
            case _          => Serialization.write(source.asInstanceOf[AnyRef])(defaultFormats)
          }
          case v: JValue => JsonMethods.compact(JsonMethods.render(v))
          case s: String => s
          case _         => Serialization.write(source.asInstanceOf[AnyRef])(defaultFormats)
        }
        serialized.getBytes(charset)
      }

      override def writeInto(responseBody: OutputStream) = {
        val length = if (contents._2 > 0) contents._2 else 8192
        IOUtils.copy(contents._1, responseBody, length)
        responseBody.flush
      }
      override def getContentLength = contents._2
    }
  }

}
