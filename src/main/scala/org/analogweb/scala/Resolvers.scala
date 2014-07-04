package org.analogweb.scala

import org.analogweb.core.ParameterValueResolver
import org.analogweb.core.PathVariableValueResolver
import org.analogweb.core.CookieValueResolver
import org.analogweb.core.RequestBodyValueResolver

trait Resolvers {

  protected def parameter = classOf[ParameterValueResolver]

  protected def path = classOf[PathVariableValueResolver]

  protected def cookie = classOf[CookieValueResolver]

  protected def body = classOf[RequestBodyValueResolver]

}

