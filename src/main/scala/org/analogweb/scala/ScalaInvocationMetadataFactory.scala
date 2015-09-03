package org.analogweb.scala

import java.util.{ Collection, Collections }
import scala.util.Try
import scala.collection.mutable.ArrayBuffer
import scala.collection.convert.decorateAsJava._
import org.analogweb.{ ContainerAdaptor, InvocationMetadata, InvocationMetadataFactory }
import org.analogweb.core.DefaultInvocationMetadata

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {

  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[RouteDef].isAssignableFrom(clazz) && classOf[RouteDef].getCanonicalName != clazz.getCanonicalName
  }

  def createInvocationMetadatas(clazz: Class[_], instances: ContainerAdaptor): Collection[InvocationMetadata] = {
    clazz match {
      case c if (classOf[RouteDef].isAssignableFrom(c) && classOf[Analogweb].getCanonicalName() != c.getCanonicalName()) => {
        val routes = obtainInstance(c, instances).routes
        val metadatas = routes.map(route =>
          new DefaultScalaInvocationMetadata(clazz, s"${route.method}(${route.rawPath})", Array(), route)).toSeq
        asJavaCollectionConverter[InvocationMetadata](metadatas).asJavaCollection
      }
      case _ => new java.util.ArrayList[InvocationMetadata]
    }
  }

  private def obtainInstance(c: Class[_], instances: ContainerAdaptor): RouteDef = {
    Try {
      Option(instances.getInstanceOfType(c)).map(_.asInstanceOf[RouteDef]).getOrElse(instantiate(c))
    }.getOrElse(instantiate(c))
  }

  private def instantiate(c: Class[_]): RouteDef = {
    try {
      c.getField("MODULE$").get(c).asInstanceOf[RouteDef]
    } catch {
      case e: NoSuchFieldException => c.newInstance.asInstanceOf[RouteDef]
    }
  }
}
