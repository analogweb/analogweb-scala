package org.analogweb

import java.lang.annotation.Annotation
import java.io.{ File, InputStream, OutputStream, FileInputStream, ByteArrayInputStream }
import reflect.ClassTag
import language.implicitConversions
import org.analogweb._, util._, core._, scala._
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.{ JsonMethods, Serialization }

package object json4s {

  // Resolving JSON requests.
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

  // Serializing JSON responses.
  def asJson(obj: AnyRef)(implicit formats: Formats = Serialization.formats(NoTypeHints)) = new ScalaJsonObject((obj, formats))
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)

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
            case (obj, formats: Formats) => Serialization.write(obj.asInstanceOf[AnyRef])(formats)
            case (obj, _)                => Serialization.write(source.asInstanceOf[AnyRef])(defaultFormats)
            case v: JValue               => JsonMethods.compact(JsonMethods.render(v))
            case s: String               => s
            case _                       => Serialization.write(source.asInstanceOf[AnyRef])(defaultFormats)
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
}
