package org.analogweb.scala

import scala.collection.mutable.Map
import scala.collection.convert.decorateAsJava._
import org.analogweb.Renderable
import org.analogweb.core.response._
import org.analogweb.ResponseFormatter
import org.analogweb.RequestContext
import org.analogweb.ResponseContext
import org.analogweb.ResponseContext.ResponseEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonMappingException
import java.io.OutputStream

object Responses {
  def asText(obj: String) = Text.`with`(obj)
  def asHtmlEntity(obj: String) = Html.`with`(obj)
  def asHtml(templatePath: String) = Html.as(templatePath)
  def asHtml(templatePath: String, context: Map[String, AnyRef]) = Html.as(templatePath, context.asJava)
  def asJson(obj: AnyRef) = new ScalaJson(obj)
  def asXml(obj: AnyRef) = Xml.as(obj)
  def redirectTo(url: String) = Redirect.to(url)
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

class ScalaJson(obj: AnyRef) extends Json(obj) {
}

class ScalaJsonFormatter extends ResponseFormatter {

  protected val objectMapper: ObjectMapper = {
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
