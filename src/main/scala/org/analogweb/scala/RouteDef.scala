package org.analogweb.scala

import scala.collection.mutable.ListBuffer

trait RouteDef {

  def get(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("GET", path, arounds)(action))

  def post(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("POST", path, arounds)(action))

  def put(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("PUT", path, arounds)(action))

  def delete(path: String)(action: Request => Any)(implicit arounds: Arounds = Arounds()) = register(Route("DELETE", path, arounds)(action))

  private def register(route: Route) = {
    routes += route
    route
  }

  protected[scala] val routes = ListBuffer[Route]()

}
