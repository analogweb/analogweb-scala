package org.analogweb.scala

import scala.reflect.ClassTag
import org.analogweb.RequestValueResolver

case class Scope[T <: RequestValueResolver](val resolverType: Class[T], val r: Request) {

  def valueOf(name: String): Option[String] = valueAs[String](name)

  def valueAs[T](implicit ctag: ClassTag[T]): Option[T] = {
    valueAs("")(ctag)
  }

  def valueAs[T](name: String)(implicit ctag: ClassTag[T]): Option[T] = {
    Some(r.resolvers.findRequestValueResolver(resolverType)).map { resolver =>
      Option(resolver.resolveValue(r.context, r.metadata, name, ctag.runtimeClass, Array()).asInstanceOf[T])
    }.getOrElse(throw new IllegalArgumentException)
  }

}

