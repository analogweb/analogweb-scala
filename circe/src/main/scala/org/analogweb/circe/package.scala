package analogweb

import language.implicitConversions
import io.circe.{Decoder, Encoder, Json}
import org.analogweb.scala.{Request, InstanceResolverSyntax, ScalaJsonObject, ScalaJsonText}
import org.analogweb.circe._

package object circe {

  // Resolving JSON requests.
  val json = new CirceJsonValueResolver()

  implicit def asCirceJsonValueResolverSyntax[A](
      resolver: CirceJsonValueResolver
  )(implicit request: Request, decoder: Decoder[A]): InstanceResolverSyntax[CirceJsonValueResolver] =
    InstanceResolverSyntax(resolver, request, CirceResolverContext(decoder))

  // Serializing JSON responses.
  def asJson[A](source: A)(implicit encoder: Encoder[A]) =
    new ScalaJsonObject((source, encoder))
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)
  def asJson(json: Json)       = new ScalaJsonObject(json)

}
