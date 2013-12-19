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
      val instance = clazz.newInstance()
      val metadatas = ArrayBuffer[InvocationMetadata]()
      instance match {
        case an: Analogweb => an.actionsMap.foreach(x => metadatas.append(new ScalaInvocationMetadata(clazz,"noop",null,x._1,x._2)))
        case _ => throw new ClassCastException
      }
      metadatas
    }

    def createInvocationMetadata(clazz:Class[_],m:java.lang.reflect.Method):InvocationMetadata = {
      //nop.
      new DefaultInvocationMetadata(null,null,null,null)
    }
}
