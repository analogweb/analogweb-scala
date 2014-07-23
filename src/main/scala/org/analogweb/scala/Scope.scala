package org.analogweb.scala

import scala.reflect.ClassTag
import org.analogweb.RequestValueResolver
import org.analogweb.MediaType
import org.analogweb.core.SpecificMediaTypeRequestValueResolver
import org.analogweb.core.UnsupportedMediaTypeException

trait Scope[T <: RequestValueResolver] {

  def resolverType: Class[T]
  def request: Request
  def of(name: String): Option[String] = as[String](name)

  def as[T](implicit ctag: ClassTag[T]): Option[T] = {
    as("")(ctag)
  }

  def as[T](name: String)(implicit ctag: ClassTag[T]): Option[T] = {
    Option(request.resolvers.findRequestValueResolver(resolverType)).map { implicit resolver =>
      verifyMediaType
      Option(resolver.resolveValue(request.context, request.metadata, name, ctag.runtimeClass, Array()).asInstanceOf[T])
    }.getOrElse(throw new IllegalArgumentException)
  }

  private def verifyMediaType(implicit resolver: RequestValueResolver) = {
    resolver match {
      case x: SpecificMediaTypeRequestValueResolver => request.contentType.map { c =>
        if (x.supports(c) == false) throw new UnsupportedMediaTypeException(request.requestPath)
      }
      case _ => // nop.
    }
  }

}

case class DefaultScope[T <: RequestValueResolver](override val resolverType: Class[T], override val request: Request) extends Scope[T]

case class MappingScope[T <: MappingRequestValueResolver](override val resolverType: Class[T], override val request: Request) extends Scope[T] {

  def to[S](f: Request => S) = f(request)

}
