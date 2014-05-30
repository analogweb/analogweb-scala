package org.analogweb.scala

import org.analogweb.core.RequestPathDefinition
import org.analogweb.RequestValueResolver
import org.analogweb.core.ParameterValueResolver
import org.analogweb.core.PathVariableValueResolver
import org.analogweb.core.CookieValueResolver
import org.analogweb.core.RequestBodyValueResolver

trait Analogweb {

  implicit def path2route(path: String): RequestPathDefinition = RequestPathDefinition.define("/", path)

  def get(path: RequestPathDefinition)(action: Request => Any) = Route("GET", path)(action)

  def post(path: RequestPathDefinition)(action: Request => Any) = Route("POST", path)(action)

  def put(path: RequestPathDefinition)(action: Request => Any) = Route("PUT", path)(action)

  def delete(path: RequestPathDefinition)(action: Request => Any) = Route("DELETE", path)(action)

  implicit def asScope[T <: RequestValueResolver](typeOfResolver: Class[T])(implicit request: Request) = Scope(typeOfResolver, request)

  val parameter = classOf[ParameterValueResolver]

  val path = classOf[PathVariableValueResolver]

  val cookie = classOf[CookieValueResolver]

  val body = classOf[RequestBodyValueResolver]
}
