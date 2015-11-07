package org.analogweb.scala

import org.analogweb.core.RequestPathDefinition

trait Route {

  val method: String

  val rawPath: String

  val path = RequestPathDefinition.define("/", rawPath, Array[String](method))

  def invoke(request: Request): Any

}

class RequestInvocation(override val method: String, override val rawPath: String, val arounds: Arounds)(val invocation: Request => Any) extends Route {
  override def invoke(request: Request): Any = invocation(request)
  def :+(around: Around): RequestInvocation = new RequestInvocation(method, rawPath, arounds :+ around)(invocation)
}

object Route {

  def apply(method: String, path: String, arounds: Arounds)(invocation: Request => Any) = new RequestInvocation(method, path, arounds)(invocation)

}

