package org.analogweb.scala

import collection.JavaConversions._
import org.analogweb.InvocationMetadata
import org.analogweb.InvocationMetadataFactory
import org.analogweb.core.DefaultInvocationMetadata
import java.util
import scala.collection.mutable.ArrayBuffer

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {
  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[Analogweb].isAssignableFrom(clazz) && classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
  }

  def createInvocationMetadatas(clazz: Class[_]): util.Collection[InvocationMetadata] = {
    val metadatas = ArrayBuffer[InvocationMetadata]()
    val classOfAnalogweb = classOf[Analogweb]
    val instance = clazz.newInstance
    clazz match {
      case classOfAnalogweb => {
        clazz.getDeclaredMethods.filter(m =>
          classOf[Route].isAssignableFrom(m.getReturnType)).filter(m =>
          Seq("get", "post", "put", "delete").contains(m.getName) == false).map(f =>
          metadatas.append(new ScalaInvocationMetadata(clazz, f.getName, null, f.invoke(instance).asInstanceOf[Route])))
      }
    }
    metadatas
  }

}
