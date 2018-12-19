package org.analogweb.circe

import java.nio.charset.Charset
import java.io.InputStream
import scala.util.{Right, Left}
import org.analogweb._, core._, core.DefaultReadableBuffer._, util._, util.logging._, scala._
import io.circe.{jawn, Decoder, Encoder, Json}

case class CirceResolverContext[A](val decoder: Decoder[A]) extends ResolverContext

class CirceJsonValueResolver extends ScalaRequestValueResolver {

  def circeResolverContext2Decoder[A](context: ResolverContext): Option[Decoder[A]] =
    context match {
      case CirceResolverContext(decoder) => Some(decoder.asInstanceOf[Decoder[A]])
      case _                             => None
    }

  private[this] val classOfJson   = classOf[Json]
  private[this] val classOfString = classOf[String]

  override def resolve[T](
      request: RequestContext,
      metadata: InvocationMetadata,
      key: String,
      requiredType: Class[T]
  )(implicit context: ResolverContext): Either[NoValuesResolved[T], T] = {
    val body = request.getRequestBody.asString(Charset.forName("UTF-8"))
    jawn
      .parse(body)
      .right
      .flatMap { parsed =>
        requiredType match {
          case `classOfJson`   => Right(parsed.asInstanceOf[T])
          case `classOfString` => Right(parsed.noSpaces.asInstanceOf[T])
          case _ => {
            circeResolverContext2Decoder[T](context)
              .map { c =>
                c.decodeJson(parsed)
              }
              .getOrElse {
                Left(NoValuesResolved(key, this, requiredType))
              }
          }
        }
      }
      .left
      .map { parsingError =>
        NoValuesResolved(key, this, requiredType)
      }
  }

  override def supports(contentType: MediaType) =
    MediaTypes.APPLICATION_JSON_TYPE.isCompatible(contentType)

}

class CirceJsonFormatter extends ResponseFormatter {

  override def formatAndWriteInto(request: RequestContext,
                                  response: ResponseContext,
                                  charset: String,
                                  source: Any): ResponseEntity[_] = {
    lazy val toBytes = {
      val serialized: String = source match {
        case (obj: Any, formats: Encoder[Any]) => formats(obj).noSpaces
        case v: Json                           => v.noSpaces
        case s: String                         => s
        case _                                 => "{}"
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

class CirceModuleConfig extends PluginModulesConfig {

  val messageLog = new PropertyResourceBundleMessageResource("org.analogweb.circe.analog-messages")
  val log        = Logs.getLog(classOf[CirceModuleConfig])

  def prepare(builder: ModulesBuilder): ModulesBuilder = {
    log.log(messageLog, "ISB000001")
    builder
      .addResponseFormatterClass(classOf[ScalaJsonObject], classOf[CirceJsonFormatter])
      .addResponseFormatterClass(classOf[ScalaJsonText], classOf[CirceJsonFormatter])
  }

}
