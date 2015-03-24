package org.analogweb.scala

import org.analogweb.core.RequestPathDefinition

trait Route {

  val method: String

  val rawPath: String

  val path = RequestPathDefinition.define("/", rawPath, Array[String](method))

  def invoke(request: Request): Any

}

class RequestInvocation(override val method: String, override val rawPath: String)(val invocation: Request => Any) extends Route {
  override def invoke(request: Request): Any = invocation(request)
}

object Route {

  def apply(method: String, path: String)(invocation: Request => Any) = new RequestInvocation(method, path)(invocation)

}

