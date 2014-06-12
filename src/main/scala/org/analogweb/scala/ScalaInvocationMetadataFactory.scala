package org.analogweb.scala

import collection.JavaConversions._
import org.analogweb.InvocationMetadata
import org.analogweb.InvocationMetadataFactory
import org.analogweb.core.DefaultInvocationMetadata
import java.util.Collection
import java.util.Collections
import scala.collection.mutable.ArrayBuffer

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {

  val ignoreMethods = Seq("get", "post", "put", "delete")

  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[Analogweb].isAssignableFrom(clazz) && classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
  }

  private[this] val ClassOfAnalogweb = classOf[Analogweb]

  def createInvocationMetadatas(clazz: Class[_]): Collection[InvocationMetadata] = {
    clazz match {
      case ClassOfAnalogweb => {
        val ms = clazz.getDeclaredMethods.filter(m =>
          classOf[Route].isAssignableFrom(m.getReturnType)).filter(m =>
          ignoreMethods.contains(m.getName) == false
        )
        val instance = clazz.newInstance
        ms.map(f =>
          new ScalaInvocationMetadata(clazz, f.getName, null, f.invoke(instance).asInstanceOf[Route])).toSeq
      }
      case _ => new java.util.ArrayList[InvocationMetadata]
    }
  }

}
