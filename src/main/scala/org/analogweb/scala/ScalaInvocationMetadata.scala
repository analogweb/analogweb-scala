package org.analogweb.scala

import org.analogweb.core.DefaultInvocationMetadata
import org.analogweb.InvocationMetadata

trait ScalaInvocationMetadata extends InvocationMetadata {
  def route: Route
}

case class DefaultScalaInvocationMetadata(clazz: Class[_],
                                          name: String,
                                          argumentTypes: Array[Class[_]],
                                          override val route: Route)
    extends DefaultInvocationMetadata(clazz, name, argumentTypes, route.path)
    with ScalaInvocationMetadata {}
