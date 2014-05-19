package org.analogweb.scala

import collection.JavaConversions._
import org.analogweb.InvocationMetadata
import org.analogweb.InvocationMetadataFactory
import org.analogweb.core.DefaultInvocationMetadata
import java.util
import scala.collection.mutable.ArrayBuffer

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {
    def containsInvocationClass(clazz:Class[_]):Boolean = {
      classOf[Analogweb].isAssignableFrom(clazz) && classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
    }

    def createInvocationMetadatas(clazz:Class[_]):util.Collection[InvocationMetadata] = {
      val metadatas = ArrayBuffer[InvocationMetadata]()
      val classOfAnalogweb = classOf[Analogweb]
      clazz match {
        case classOfAnalogweb => {
            val methods = clazz.getMethods.filter(m => classOf[Route].isAssignableFrom(m.getReturnType))
            methods.foreach(m => m.invoke(clazz.newInstance) match { case r:Route => metadatas.append(new ScalaInvocationMetadata(clazz,m.getName,null,r))})
        }
      }
      metadatas
    }

}
