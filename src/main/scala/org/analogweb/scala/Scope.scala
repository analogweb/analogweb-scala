package org.analogweb.scala

import org.analogweb.RequestValueResolver

case class Scope[T <: RequestValueResolver](val resolverType: Class[T], val r: Request) {

  def value(name:String) = valueAs(name,classOf[String])
  def valueAs(name: String, pe: Class[_]) = {
    Some(r.resolvers.findRequestValueResolver(resolverType)).map { resolver =>
      resolver.resolveValue(r.context, r.metadata, name, pe)
    }.getOrElse(throw new IllegalArgumentException)
  }

}

