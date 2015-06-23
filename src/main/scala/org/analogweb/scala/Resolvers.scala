package org.analogweb.scala

import java.lang.annotation.Annotation
import org.analogweb.{ InvocationMetadata, MediaType, RequestContext, RequestValueResolver, TypeMapper }
import org.analogweb.core.{ MediaTypes, SpecificMediaTypeRequestValueResolver, ParameterValueResolver, PathVariableValueResolver, CookieValueResolver, RequestBodyValueResolver, XmlValueResolver, RequestContextValueResolver, MultipartParameterResolver }
import org.json4s._
import org.json4s.jackson.JsonMethods

trait Resolvers {

  protected def parameter = classOf[ParameterValueResolver]

  protected def path = classOf[PathVariableValueResolver]

  protected def cookie = classOf[CookieValueResolver]

  protected def body = classOf[RequestBodyValueResolver]

  protected def xml = classOf[XmlValueResolver]

  protected def json = classOf[ScalaJacksonJsonValueResolver]

  protected def multipart = classOf[MultipartParameterResolver]

  protected def context = classOf[RequestContextValueResolver]

}

class ScalaJacksonJsonValueResolver extends SpecificMediaTypeRequestValueResolver {

  implicit val formats = DefaultFormats

  override def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = {
    val parsed = JsonMethods.parse(request.getRequestBody)
    requiredType match {
      case x if x == classOf[JObject] => parsed
      case y if y == classOf[JValue]  => parsed
      case z                          => parsed.extract(formats, Manifest.classType(requiredType))
    }
  }

  override def supports(contentType: MediaType) = MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType);

}
