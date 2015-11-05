package org.analogweb.scala

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import org.analogweb.{ Renderable, RequestValueResolver }

trait RouteDef {

  def get(path: String)(action: Request => Any) = register(Route("GET", path)(action))

  def post(path: String)(action: Request => Any) = register(Route("POST", path)(action))

  def put(path: String)(action: Request => Any) = register(Route("PUT", path)(action))

  def delete(path: String)(action: Request => Any) = register(Route("DELETE", path)(action))

  implicit def response(f: => Any) = { implicit r: Request => f }

  private def register(route: Route) = {
    routes += route
    route
  }

  protected[scala] val routes = ListBuffer[Route]()

  implicit def asScope[T <: RequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = DefaultScope(typeOfResolver, request)

  implicit def asRequestObjectMapping[T](mapping: Request => T)(implicit request: Request) = mapping(request)

}

trait Rejection
case class reject(a: Renderable) extends Rejection
case class pass() extends Rejection

trait Around
case class before(action: Request => Rejection) extends Around
case class after(action: PartialFunction[Any, Renderable]) extends Around

