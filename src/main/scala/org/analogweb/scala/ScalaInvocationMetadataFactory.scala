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
      // TODO pick up all Route within instance.
      val hello = new Hello
      val metadatas = ArrayBuffer[InvocationMetadata]()
      instance match {
        case an: Analogweb => metadatas.append(new ScalaInvocationMetadata(clazz,"hello",null,hello.hello))
        case _ => throw new ClassCastException
      }
      metadatas
    }

}
