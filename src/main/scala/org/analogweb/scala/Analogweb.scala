package org.analogweb.scala

import org.analogweb.RequestValueResolver

trait Analogweb {

  def get(path: String)(action: Request => Any) = Route("GET", path)(action)

  def post(path: String)(action: Request => Any) = Route("POST", path)(action)

  def put(path: String)(action: Request => Any) = Route("PUT", path)(action)

  def delete(path: String)(action: Request => Any) = Route("DELETE", path)(action)

  implicit def asScope[T <: RequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = DefaultScope(typeOfResolver, request)

  implicit def asMappingScope[T <: MappingRequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = MappingScope(typeOfResolver, request)

}
