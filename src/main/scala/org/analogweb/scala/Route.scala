package org.analogweb.scala

import org.analogweb.core.RequestPathDefinition

trait Route {

  val method: String

  val rawPath: String

  val path = RequestPathDefinition.define("/", rawPath, Array[String](method))

  def invoke(request: Request): Any

}

class RequestInvocation(override val method: String, override val rawPath: String, val arounds: Arounds)(val invocation: Request => Any) extends Route {
  override def invoke(request: Request): Any = {
    val rejection = arounds.allBefore.map(_.action(request)).collect { case r: reject => r }.headOption.map(_.a)
    val invocationResult = rejection.getOrElse(invocation(request))
    arounds.allAfter.filter(_.action.isDefinedAt(invocationResult)).headOption.getOrElse(invocationResult)
  }
  def :+(around: Around): RequestInvocation = new RequestInvocation(method, rawPath, arounds :+ around)(invocation)
}

object Route {

  def apply(method: String, path: String, arounds: Arounds)(invocation: Request => Any) = new RequestInvocation(method, path, arounds)(invocation)

}

