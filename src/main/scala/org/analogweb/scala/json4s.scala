package org.analogweb

import java.lang.annotation.Annotation
import language.implicitConversions
import org.analogweb.core._
import org.analogweb.scala._
import org.json4s._
import org.json4s.jackson.JsonMethods

package object json4s {

  val json = classOf[Json4SJsonValueResolver]

  case class Json4SResolverContext(val formats: Formats) extends ResolverContext

  implicit def Formats2Json4SResolverContext(formats: Formats): Json4SResolverContext = Json4SResolverContext(formats)
  implicit def Json4SResolverContext2Format(context: ResolverContext): Formats = context match {
    case Json4SResolverContext(formats) => formats
    case _                              => DefaultFormats
  }

  class Json4SJsonValueResolver extends ScalaRequestValueResolver {

    override def resolve(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_])(implicit context: ResolverContext): AnyRef = {
      val parsed = JsonMethods.parse(request.getRequestBody)
      requiredType match {
        case x if x == classOf[JObject] => parsed
        case y if y == classOf[JValue]  => parsed
        case _                          => parsed.extract(context, Manifest.classType(requiredType))
      }
    }

    override def supports(contentType: MediaType) = MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType);

  }

}
