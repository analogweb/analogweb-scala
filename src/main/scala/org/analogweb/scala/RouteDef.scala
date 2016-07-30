package org.analogweb.scala

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import org.analogweb.Renderable

trait RouteDef {
  type T
  def connect(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("CONNECT", path, arounds)(action))
  def delete(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("DELETE", path, arounds)(action))
  def get(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("GET", path, arounds)(action))
  def head(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("HEAD", path, arounds)(action))
  def options(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("OPTIONS", path, arounds)(action))
  def patch(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("PATCH", path, arounds)(action))
  def post(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("POST", path, arounds)(action))
  def put(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("PUT", path, arounds)(action))
  def trace(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(Route("TRACE", path, arounds)(action))
  def scope(path: String)(routeList: RouteList) = routeList.buffer.map { x =>
    unRegister(x)
    x.update(path)
  }.foreach(register)

  protected def register(route: Route) = {
    routes += route
    route
  }

  protected def unRegister(route: Route) = {
    routes -= route
    route
  }

  protected[scala] val routes = ListBuffer[Route]()

  implicit def toRouteList(route: Route) = {
    unRegister(route)
    RouteList(ListBuffer(route))
  }
}

case class RouteList(val buffer: ListBuffer[Route]) {
  def ~(nextRoute: Route) = {
    this.buffer += nextRoute
    this
  }
}

trait LooseRouteDef extends RouteDef {
  type T = Any
}

trait StrictRouteDef extends RouteDef {
  type T = Renderable
}
