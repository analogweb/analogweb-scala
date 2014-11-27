package org.analogweb.scala

import java.io.OutputStream
import scala.collection.mutable.Map
import scala.collection.convert.decorateAsJava._
import org.analogweb.{ Renderable, ResponseFormatter, RequestContext, ResponseContext }
import org.analogweb.ResponseContext.ResponseEntity
import org.analogweb.core.response._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonMappingException

object Responses {
  def asText(obj: String) = Text.`with`(obj)
  def asHtmlEntity(obj: String) = Html.`with`(obj)
  def asHtml(templatePath: String) = Html.as(templatePath)
  def asHtml(templatePath: String, context: Map[String, AnyRef]) = Html.as(templatePath, context.asJava)
  def asJson(obj: AnyRef) = new ScalaJsonObject(obj)
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)
  def asXml(obj: AnyRef) = Xml.as(obj)
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

class ScalaJsonObject(obj: AnyRef) extends Json(obj)
class ScalaJsonText(text: String) extends Json(text)

class ScalaJsonFormatter extends ResponseFormatter {

  protected val objectMapper = initObjectMapper

  private[this] def initObjectMapper = {
    val m = new ObjectMapper
    m.registerModule(DefaultScalaModule)
    m.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
    m
  }

  override def formatAndWriteInto(request: RequestContext, response: ResponseContext, charset: String,
    source: Any): ResponseEntity = {
    new ResponseEntity() {
      override def writeInto(responseBody: OutputStream) = {
        objectMapper.writeValue(responseBody, source)
      }
      override def getContentLength = -1
    }
  }

}
