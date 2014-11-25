package org.analogweb.scala

import org.analogweb.InvocationMetadata
import org.analogweb.InvocationMetadataFactory
import org.analogweb.core.DefaultInvocationMetadata
import java.util.Collection
import java.util.Collections
import scala.collection.mutable.ArrayBuffer
import scala.collection.convert.decorateAsJava._

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {

  val ignoreMethods = Seq("get", "post", "put", "delete")

  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[Analogweb].isAssignableFrom(clazz) && classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
  }

  def createInvocationMetadatas(clazz: Class[_]): Collection[InvocationMetadata] = {
    clazz match {
      case c if classOf[Analogweb] isAssignableFrom c => {
        val ms = c.getDeclaredMethods.filter(m =>
          classOf[Route].isAssignableFrom(m.getReturnType)).filter(m =>
          ignoreMethods.contains(m.getName) == false
        )
        val instance = clazz.newInstance
        val s = ms.map(f =>
          new DefaultScalaInvocationMetadata(clazz, f.getName, Array(), f.invoke(instance).asInstanceOf[Route])).toSeq
        asJavaCollectionConverter[InvocationMetadata](s).asJavaCollection
      }
      case _ => new java.util.ArrayList[InvocationMetadata]
    }
  }

}
