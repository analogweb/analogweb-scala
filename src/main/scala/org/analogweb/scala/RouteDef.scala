package org.analogweb.scala

import scala.collection.mutable.ListBuffer
import scala.language.implicitConversions
import org.analogweb.Renderable

case class RouteSeq(val routes: Seq[Route]) {
  def ~(nextRoute: Route): RouteSeq = RouteSeq(routes :+ nextRoute)
  def ~(nextRoutes: RouteSeq): RouteSeq = RouteSeq(routes ++ nextRoutes.routes)
  def mapRoute[T](f: Route => T) = routes.map(f)
}

trait Routes {
  def routes: RouteSeq
}

@deprecated("it will be removed in future version", "0.10.0")
trait RouteDef extends Routes {
  type T
  def connect(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.connect(path)(action)(arounds))
  def delete(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.delete(path)(action)(arounds))
  def get(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.get(path)(action)(arounds))
  def head(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.head(path)(action)(arounds))
  def options(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.options(path)(action)(arounds))
  def patch(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.patch(path)(action)(arounds))
  def post(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.post(path)(action)(arounds))
  def put(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.put(path)(action)(arounds))
  def trace(path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()) = register(analogweb.trace(path)(action)(arounds))
  def scope(path: String)(routes: RouteSeq): Unit = routes.routes.map { r =>
    unRegister(r)
    r.update(path)
  }.foreach(register)

  protected def register(route: Route) = {
    routeList += route
    route
  }

  protected def unRegister(route: Route) = {
    routeList -= route
    route
  }

  private[scala] val routeList = ListBuffer[Route]()

  override def routes = RouteSeq(routeList.toSeq)

  implicit def toRouteSeq(route: Route) = {
    unRegister(route)
    RouteSeq(Seq(route))
  }
}

@deprecated("it will be removed in future version", "0.10.0")
trait LooseRouteDef extends RouteDef {
  type T = Any
}

@deprecated("it will be removed in future version", "0.10.0")
trait StrictRouteDef extends RouteDef {
  type T = Renderable
}
