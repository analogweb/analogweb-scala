package org.analogweb.scala

import org.analogweb.core.RequestPathDefinition

trait Route {

  val method: String

  val rawPath: String

  val path = RequestPathDefinition.define("/", rawPath, Array[String](method))

  def invoke(request: Request): Any

  def update(parentPath: String): Route

}

trait Routes {
  def routes: Seq[Route]
}

class RequestInvocation(override val method: String,
                        override val rawPath: String,
                        val arounds: Arounds)(val invocation: Request => Any)
    extends Route {
  override def invoke(request: Request): Any = {
    val (rejection, passes) =
      arounds.allBefore
        .map(_.action(request))
        .partition(_.isInstanceOf[reject])
    val invocationResult = rejection.headOption
      .map(_.asInstanceOf[reject].reason)
      .getOrElse {
        val passedWithValues =
          passes
            .collect {
              case p: passWith[_] =>
                p
            }
            .map(p => p.key -> p.result)
            .toMap
        invocation(request.copy(passedWith = passedWithValues))
      }
    arounds.allAfter
      .filter(_.action.isDefinedAt(invocationResult))
      .headOption
      .map(_.action(invocationResult))
      .getOrElse(invocationResult)
  }
  override def update(parentPath: String) =
    new RequestInvocation(method, parentPath + rawPath, arounds)(invocation)
}

object Route {

  def apply(method: String, path: String, arounds: Arounds)(invocation: Request => Any) =
    new RequestInvocation(method, path, arounds)(invocation)

}
