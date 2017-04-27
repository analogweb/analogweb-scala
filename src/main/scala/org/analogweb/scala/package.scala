package object analogweb
    extends org.analogweb.scala.Resolvers
    with org.analogweb.scala.Responses
    with org.analogweb.scala.RouteExtensions
    with org.analogweb.scala.ServerApplications {

  import java.net.URI
  import scala.language.implicitConversions
  import scala.collection.JavaConverters._
  import org.analogweb._, scala._
  import org.analogweb.core.Servers
  import org.analogweb.core.DefaultApplicationProperties._
  import org.analogweb.core.DefaultApplicationContext._
  import org.analogweb.util.Maps

  def connect[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("CONNECT", path, arounds)(action)
  def delete[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("DELETE", path, arounds)(action)
  def get[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("GET", path, arounds)(action)
  def head[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("HEAD", path, arounds)(action)
  def options[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("OPTIONS", path, arounds)(action)
  def patch[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("PATCH", path, arounds)(action)
  def post[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("POST", path, arounds)(action)
  def put[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("PUT", path, arounds)(action)
  def trace[T](path: String)(action: Request => T)(implicit arounds: Arounds = Arounds()): Route = Route("TRACE", path, arounds)(action)
  def scope[T](path: String)(routes: RouteSeq): RouteSeq = RouteSeq(routes.routes.map(_.update(path)))

  implicit def toRouteSeq(route: Route): RouteSeq = RouteSeq(Seq(route))
  implicit def toRoutes(route: Route): Routes = new Routes { val routes = RouteSeq(Seq(route)) }
  implicit def toRoutes(routeSeq: RouteSeq): Routes = new Routes { val routes = routeSeq }

}
