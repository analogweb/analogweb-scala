package org.analogweb.scala

import org.analogweb.core.DefaultInvocationMetadata

class ScalaInvocationMetadata(clazz: Class[_], name: String, argumentTypes: Array[Class[_]], val route: Route)
    extends DefaultInvocationMetadata(clazz, name, argumentTypes, route.path) {
}
