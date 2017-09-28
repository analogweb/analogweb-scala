package analogweb

import java.lang.annotation.Annotation
import java.io.{ File, InputStream, OutputStream, FileInputStream, ByteArrayInputStream }
import reflect.ClassTag
import language.implicitConversions
import io.circe.{ jawn, Decoder, Encoder, Errors, Json, Printer }

package object circe {

  import org.analogweb._, util._, core._, core.DefaultReadableBuffer._, scala._, circe._

  // Resolving JSON requests.
  val json = classOf[CirceJsonValueResolver]

  implicit def asCirceJsonValueResolverSyntax[A](
    typeOfResolver: Class[CirceJsonValueResolver]
  )(implicit request: Request, decoder: Decoder[A]) = DefaultResolverSyntax(typeOfResolver, request, CirceResolverContext(decoder))

  // Serializing JSON responses.
  def asJson[A](source: AnyRef)(implicit encoder: Encoder[A]) = new ScalaJsonObject((source, encoder))
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)

}
