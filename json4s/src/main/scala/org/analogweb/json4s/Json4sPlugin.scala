package org.analogweb.json4s

import java.io.InputStream
import scala.language.implicitConversions
import scala.util._
import org.analogweb._, util._, util.logging._, core._, core.DefaultReadableBuffer._, scala._
import org.json4s._, jackson.{JsonMethods, Serialization}

case class Json4sResolverContext(val formats: Formats) extends ResolverContext

class Json4sJsonValueResolver extends ScalaRequestValueResolver {

  implicit def json4sResolverContext2Format(context: ResolverContext): Formats = context match {
    case Json4sResolverContext(formats) => formats
    case _                              => DefaultFormats
  }

  override def resolve[A](
      request: RequestContext,
      metadata: InvocationMetadata,
      key: String,
      requiredType: Class[A]
  )(implicit context: ResolverContext): Either[NoValuesResolved[A], A] = {
    JsonMethods
      .parseOpt(request.getRequestBody.asInputStream)
      .map(parsed => Right(parsed.extract[A](context, Manifest.classType(requiredType))))
      .getOrElse(Left(NoValuesResolved(key, this, requiredType)))
  }

  override def supports(contentType: MediaType) =
    MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType);

}

class Json4sJsonFormatter extends ResponseFormatter {

  val defaultFormats = Serialization.formats(NoTypeHints)

  override def formatAndWriteInto(request: RequestContext,
                                  response: ResponseContext,
                                  charset: String,
                                  source: Any): ResponseEntity[_] = {

    lazy val toBytes = {
      val serialized = source match {
        case (obj, formats: Formats) => Serialization.write(obj.asInstanceOf[AnyRef])(formats)
        case (obj, _)                => Serialization.write(source.asInstanceOf[AnyRef])(defaultFormats)
        case v: JValue               => JsonMethods.compact(JsonMethods.render(v))
        case s: String               => s
        case _                       => Serialization.write(source.asInstanceOf[AnyRef])(defaultFormats)
      }
      serialized.getBytes(charset)
    }

    source match {
      case r: ReadableBuffer => new ReadableBufferResponseEntity(r)
      case i: InputStream    => new ReadableBufferResponseEntity(readBuffer(i))
      case _                 => new DefaultResponseEntity(toBytes)
    }
  }
}

class Json4sModuleConfig extends PluginModulesConfig {

  val messageLog = new PropertyResourceBundleMessageResource("org.analogweb.scala.analog-messages")
  val log        = Logs.getLog(classOf[Json4sModuleConfig])

  def prepare(builder: ModulesBuilder): ModulesBuilder = {
    log.log(messageLog, "ISB000001")
    builder
      .addResponseFormatterClass(classOf[ScalaJsonObject], classOf[Json4sJsonFormatter])
      .addResponseFormatterClass(classOf[ScalaJsonText], classOf[Json4sJsonFormatter])
  }

}
