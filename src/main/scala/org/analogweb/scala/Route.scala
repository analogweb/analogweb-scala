package org.analogweb.scala

import org.analogweb.core.RequestPathDefinition

class Route(val method: String, val rawPath: String)(invocation: Request => Any) {

  val path = RequestPathDefinition.define("/", rawPath, Array[String](method))

  def invoke(request: Request): Any = invocation(request)

}

object Route {

  def apply(method: String, path: String)(invocation: Request => Any) = new Route(method, path)(invocation)

}

