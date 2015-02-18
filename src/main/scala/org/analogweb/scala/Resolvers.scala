package org.analogweb.scala

import java.lang.annotation.Annotation
import org.analogweb.{ InvocationMetadata, MediaType, RequestContext, RequestValueResolver, TypeMapper }
import org.analogweb.core.{ MediaTypes, SpecificMediaTypeRequestValueResolver, ParameterValueResolver, PathVariableValueResolver, CookieValueResolver, RequestBodyValueResolver, XmlValueResolver, RequestContextValueResolver }
import org.analogweb.acf.{ MultipartParameterResolver, MultipartParameterStreamResolver }
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.core.JsonParseException

trait Resolvers {

  protected def parameter = classOf[ParameterValueResolver]

  protected def path = classOf[PathVariableValueResolver]

  protected def cookie = classOf[CookieValueResolver]

  protected def body = classOf[RequestBodyValueResolver]

  protected def xml = classOf[XmlValueResolver]

  protected def json = classOf[ScalaJacksonJsonValueResolver]

  protected def multipart = classOf[MultipartParameterResolver]

  protected def smultipart = classOf[MultipartParameterStreamResolver]

  protected def mapping = classOf[MappingRequestValueResolver]

  protected def context = classOf[RequestContextValueResolver]

}

trait ScalaValueResolver extends RequestValueResolver {
  override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = {
    // nop.
    null
  }

  def resolveValue(request: Request, key: String, requiredType: Class[_])

}

class MappingRequestValueResolver extends ScalaValueResolver {

  def resolveValue(request: Request, key: String, requiredType: Class[_]) = {
    // TODO implement auto mapping.
  }

}

class ScalaJacksonJsonValueResolver extends SpecificMediaTypeRequestValueResolver {

  protected val objectMapper: ObjectMapper = initObjectMapper
  private[this] def initObjectMapper = {
    val m = new ObjectMapper
    m.registerModule(DefaultScalaModule)
    m
  }

  override def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = {
    try {
      objectMapper.readValue(request.getRequestBody, requiredType).asInstanceOf[AnyRef]
    } catch {
      case e: JsonMappingException => null
      case e: JsonParseException   => null
    }
  }

  override def supports(contentType: MediaType) = MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType);

}
