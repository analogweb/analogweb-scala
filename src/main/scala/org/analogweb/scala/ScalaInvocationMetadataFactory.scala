package org.analogweb.scala

import java.util.Collection
import java.util.Collections
import scala.collection.mutable.ArrayBuffer
import scala.collection.convert.decorateAsJava._
import org.analogweb.{ ContainerAdaptor, InvocationMetadata, InvocationMetadataFactory }
import org.analogweb.core.DefaultInvocationMetadata

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {

  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[Analogweb].isAssignableFrom(clazz) && classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
  }

  def createInvocationMetadatas(clazz: Class[_], instances: ContainerAdaptor): Collection[InvocationMetadata] = {
    clazz match {
      case c if classOf[Analogweb] isAssignableFrom c => {
        val routes = obtainInstance(c, instances).routes
        val metadatas = routes.map(route =>
          new DefaultScalaInvocationMetadata(clazz, s"${route.method}(${route.rawPath})", Array(), route)).toSeq
        asJavaCollectionConverter[InvocationMetadata](metadatas).asJavaCollection
      }
      case _ => new java.util.ArrayList[InvocationMetadata]
    }
  }

  private def obtainInstance(c: Class[_], instances: ContainerAdaptor): Analogweb = {
    Option(instances.getInstanceOfType(c)).map(_.asInstanceOf[Analogweb]).getOrElse(instantiate(c))
  }

  private def instantiate(c: Class[_]): Analogweb = {
    try {
      c.getField("MODULE$").get(c).asInstanceOf[Analogweb]
    } catch {
      case e: NoSuchFieldException => c.newInstance.asInstanceOf[Analogweb]
    }
  }
}
