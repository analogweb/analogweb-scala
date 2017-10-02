package analogweb

import java.lang.annotation.Annotation
import java.io.{ File, InputStream, OutputStream, FileInputStream, ByteArrayInputStream }
import reflect.ClassTag
import language.implicitConversions
import io.circe.{ jawn, Decoder, Encoder, Errors, Json, Printer }
import org.analogweb.scala._
import org.analogweb.circe._

package object circe {

  // Resolving JSON requests.
  val json = classOf[CirceJsonValueResolver]

  implicit def asCirceJsonValueResolverSyntax[A](
    typeOfResolver: Class[CirceJsonValueResolver]
  )(implicit request: Request, decoder: Decoder[A]) = DefaultResolverSyntax(typeOfResolver, request, CirceResolverContext(decoder))

  // Serializing JSON responses.
  def asJson[A](source: AnyRef)(implicit encoder: Encoder[A]) = new ScalaJsonObject((source, encoder))
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)
  def asJson(json: Json) = new ScalaJsonObject(json)

}
