package org.analogweb.scala

import scala.language.implicitConversions
import scala.concurrent.Future
import scala.util.Try
import org.analogweb.{RequestValueResolver, Renderable}

trait RouteExtensions {
  self: Resolvers =>

  implicit def response(f: => Any): Any = { implicit r: Request =>
    f
  }

  implicit def asResolverSyntax[T <: RequestValueResolver](resolver: T)(implicit request: Request): InstanceResolverSyntax[T] =
    InstanceResolverSyntax(resolver, request)

  implicit def asRequestObjectMapping[T](mapping: Request => T)(implicit request: Request): T =
    mapping(request)

  implicit def toArounds(around: Around): Arounds =
    Arounds(Seq(around))

  implicit class FutureExtensions(future: Future[Renderable]) {
    def asRenderable() =
      RenderableFuture(future)
  }

  def param(query: String)(implicit r: Request): String = {
    parameter
      .as[String](query)
      .right
      .getOrElse(
        path.as[String](query) match {
          case Right(v) =>
            v
          case Left(t) =>
            ""
        }
      )
  }

  def passedWith[T](key: String)(implicit r: Request): Option[T] = {
    r.passedWith
      .get(key)
      .flatMap(v =>
        Try {
          v.asInstanceOf[T]
        }.toOption)
  }
}
