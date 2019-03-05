package analogweb

import language.implicitConversions
import org.analogweb._, scala._
import org.analogweb.json4s._
import org.json4s._, jackson.Serialization

package object json4s {

  // Resolving JSON requests.
  val json = new Json4sJsonValueResolver()

  implicit def asJson4sResolverSyntax(
      resolver: Json4sJsonValueResolver
  )(implicit request: Request, formats: Formats = DefaultFormats) =
    InstanceResolverSyntax(resolver, request, org.analogweb.json4s.Json4sResolverContext(formats))

  // Serializing JSON responses.
  def asJson(source: AnyRef)(implicit formats: Formats = Serialization.formats(NoTypeHints)) =
    new ScalaJsonObject((source, formats))
  def asJson(jsonText: String) = new ScalaJsonText(jsonText)

}
