package org.analogweb.scala

import java.util.Collection
import java.util.Collections
import scala.collection.mutable.ArrayBuffer
import scala.collection.convert.decorateAsJava._
import org.analogweb.{ InvocationMetadata, InvocationMetadataFactory }
import org.analogweb.core.DefaultInvocationMetadata

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {

  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[Analogweb].isAssignableFrom(clazz) && classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
  }

  def createInvocationMetadatas(clazz: Class[_]): Collection[InvocationMetadata] = {
    clazz match {
      case c if classOf[Analogweb] isAssignableFrom c => {
        val routes = c.newInstance.asInstanceOf[Analogweb].routes
        val metadatas = routes.map(route =>
          new DefaultScalaInvocationMetadata(clazz, s"${route.method}(${route.rawPath})", Array(), route)).toSeq
        asJavaCollectionConverter[InvocationMetadata](metadatas).asJavaCollection
      }
      case _ => new java.util.ArrayList[InvocationMetadata]
    }
  }

}
