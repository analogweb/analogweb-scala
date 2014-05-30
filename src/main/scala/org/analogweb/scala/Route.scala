package org.analogweb.scala

import org.analogweb._
import org.analogweb.core._

class Route(val method: String, val path: RequestPathMetadata)(invocation: Request => Any) {

  def invoke(request: Request): Any = invocation(request)

}

object Route {

  def apply(method: String, path: RequestPathMetadata)(invocation: Request => Any) = new Route(method, path)(invocation)

}

