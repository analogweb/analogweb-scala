package org.analogweb.scala

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import org.analogweb.{ Renderable, RequestValueResolver }

trait RouteDef {

  def get(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("GET", path, arounds)(action))

  def post(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("POST", path, arounds)(action))

  def put(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("PUT", path, arounds)(action))

  def delete(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("DELETE", path, arounds)(action))

  implicit def response(f: => Any) = { implicit r: Request => f }

  private def register(route: Route) = {
    routes += route
    route
  }

  protected[scala] val routes = ListBuffer[Route]()

  implicit def asScope[T <: RequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = DefaultScope(typeOfResolver, request)

  implicit def asRequestObjectMapping[T](mapping: Request => T)(implicit request: Request) = mapping(request)

  implicit def toArounds(around: Around) = Arounds(Seq(around))
}

trait Rejection
case class reject(a: Renderable) extends Rejection
case class pass() extends Rejection

trait Around
case class before(action: Request => Rejection) extends Around
case class after(action: PartialFunction[Any, Renderable]) extends Around

case class Arounds(arounds: Seq[Around] = Seq()) {
  def :+(around: Around) = Arounds(arounds :+ around)
  def allAfter = arounds.collect { case a: after => a }
  def allBefore = arounds.collect { case b: before => b }
}
