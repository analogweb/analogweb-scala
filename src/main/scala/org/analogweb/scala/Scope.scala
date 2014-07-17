package org.analogweb.scala

import scala.reflect.ClassTag
import org.analogweb.RequestValueResolver
import org.analogweb.MediaType
import org.analogweb.core.SpecificMediaTypeRequestValueResolver
import org.analogweb.core.UnsupportedMediaTypeException

case class Scope[T <: RequestValueResolver](val resolverType: Class[T], val r: Request) {

  def of(name: String): Option[String] = as[String](name)

  def as[T](implicit ctag: ClassTag[T]): Option[T] = {
    as("")(ctag)
  }

  def as[T](name: String)(implicit ctag: ClassTag[T]): Option[T] = {
    Option(r.resolvers.findRequestValueResolver(resolverType)).map { implicit resolver =>
      verifyMediaType
      Option(resolver.resolveValue(r.context, r.metadata, name, ctag.runtimeClass, Array()).asInstanceOf[T])
    }.getOrElse(throw new IllegalArgumentException)
  }

  private def verifyMediaType(implicit resolver: RequestValueResolver) = {
    resolver match {
      case x: SpecificMediaTypeRequestValueResolver => r.contentType.map { c =>
        if (x.supports(c) == false) throw new UnsupportedMediaTypeException(r.requestPath)
      }
      case _ => // nop.
    }
  }

}

