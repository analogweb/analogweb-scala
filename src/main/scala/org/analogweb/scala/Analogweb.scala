package org.analogweb.scala

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import org.analogweb.RequestValueResolver

trait Analogweb {

  def get(path: String)(action: Request => Any) = register(Route("GET", path)(action))

  def post(path: String)(action: Request => Any) = register(Route("POST", path)(action))

  def put(path: String)(action: Request => Any) = register(Route("PUT", path)(action))

  def delete(path: String)(action: Request => Any) = register(Route("DELETE", path)(action))

  implicit def response(f: => Any) = { implicit r: Request => f }

  private def register(route: Route) = routes += route

  protected[scala] val routes = ListBuffer[Route]()

  implicit def asScope[T <: RequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = DefaultScope(typeOfResolver, request)

  type RequestObjectMapping[T] = Request => T

  implicit def asRequestObjectMapping[T](mapping: RequestObjectMapping[T])(implicit request: Request) = RequestObjectMapper(mapping, request)

  object RequestObjectMapper {
    def apply[T](mapping: RequestObjectMapping[T], request: Request): T = mapping(request)
  }

}
