package org.analogweb.json4s

import java.lang.annotation.Annotation
import java.io.{ File, InputStream, OutputStream, FileInputStream, ByteArrayInputStream }
import scala.reflect.ClassTag
import scala.language.implicitConversions
import scala.util._
import org.analogweb._, util._, util.logging._, core._, core.DefaultReadableBuffer._, scala._
import org.json4s._, jackson.{ JsonMethods, Serialization }

case class Json4sResolverContext(val formats: Formats) extends ResolverContext

class Json4sJsonValueResolver extends ScalaRequestValueResolver {

  implicit def json4sResolverContext2Format(context: ResolverContext): Formats = context match {
    case Json4sResolverContext(formats) => formats
    case _                              => DefaultFormats
  }

  override def resolve[A](
    request:      RequestContext,
    metadata:     InvocationMetadata,
    key:          String,
    requiredType: Class[A]
  )(implicit context: ResolverContext): Either[NoValuesResolved[A], A] = {
    println(requiredType)
    val parsed: JValue = JsonMethods.parse(request.getRequestBody.asInputStream)
    requiredType match {
      case x if x == classOf[JObject] => parsed match {
        case JObject => Right(parsed.asInstanceOf[A])
        case _       => Left(NoValuesResolved(key, this, requiredType))
      }
      case y if y == classOf[JValue] => Right(parsed.asInstanceOf[A])
      case x                         => Right(parsed.extract(context, Manifest.classType(requiredType)))
    }
  }

  override def supports(contentType: MediaType) = MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType);

}

class Json4sJsonFormatter extends ResponseFormatter {

  val defaultFormats = Serialization.formats(NoTypeHints)

  override def formatAndWriteInto(request: RequestContext, response: ResponseContext, charset: String,
                                  source: Any): ResponseEntity = {
    new ResponseEntity() {

      lazy val (contents, contentsLength) = {
        source match {
          case r: ReadableBuffer => (r, r.getLength.toInt)
          case i: InputStream    => (readBuffer(i), i.available)
          case _ => {
            val bytes = toBytes
            (readBuffer(bytes), bytes.length)
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

      override def writeInto(responseBody: WritableBuffer) = {
        responseBody.from(contents)
      }
      override def getContentLength = contentsLength
    }
  }
}

class Json4sModuleConfig extends PluginModulesConfig {

  val messageLog = new PropertyResourceBundleMessageResource("org.analogweb.scala.analog-messages")
  val log = Logs.getLog(classOf[Json4sModuleConfig])

  def prepare(builder: ModulesBuilder): ModulesBuilder = {
    log.log(messageLog, "ISB000001")
    builder
      .addResponseFormatterClass(classOf[ScalaJsonObject], classOf[Json4sJsonFormatter])
      .addResponseFormatterClass(classOf[ScalaJsonText], classOf[Json4sJsonFormatter])
  }

}
