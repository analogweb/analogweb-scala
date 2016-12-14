package org.analogweb

import java.lang.annotation.Annotation
import org.analogweb.core._

object json4s {

  import org.json4s._
  import org.json4s.jackson.JsonMethods

  val json = classOf[ScalaJacksonJsonValueResolver]

  class ScalaJacksonJsonValueResolver extends SpecificMediaTypeRequestValueResolver {

    implicit val formats = DefaultFormats

    override def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = {
      val parsed = JsonMethods.parse(request.getRequestBody)
      requiredType match {
        case x if x == classOf[JObject] => parsed
        case y if y == classOf[JValue]  => parsed
        case _                          => parsed.extract(formats, Manifest.classType(requiredType))
      }
    }

    override def supports(contentType: MediaType) = MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType);

  }

}
