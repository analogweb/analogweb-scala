package org.analogweb.scala

import collection.JavaConversions._
import org.analogweb._
import org.analogweb.core._
import java.util

class ScalaInvocationMetadata(clazz: Class[_], name: String, argumentTypes: Array[Class[_]], val route: Route)
    extends DefaultInvocationMetadata(clazz, name, argumentTypes, route.path) {
}
