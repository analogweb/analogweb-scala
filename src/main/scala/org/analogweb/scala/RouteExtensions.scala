package org.analogweb.scala

import scala.language.implicitConversions
import org.analogweb.RequestValueResolver

trait RouteExtensions {

  self: Resolvers =>

  implicit def response(f: => Any) = { implicit r: Request => f }

  implicit def asScope[T <: RequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = DefaultScope(typeOfResolver, request)

  implicit def asRequestObjectMapping[T](mapping: Request => T)(implicit request: Request) = mapping(request)

  implicit def toArounds(around: Around) = Arounds(Seq(around))

  def param(query: String)(implicit r: Request): String = {
    parameter.as[String](query).getOrElse(
      path.as[String](query).getOrElse(""))
  }

}
