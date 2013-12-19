package org.analogweb.scala 

import collection.JavaConversions._
import org.analogweb._
import org.analogweb.core._
import java.util

class ScalaInvocationMetadata(clazz:Class[_],name:String,argumentTypes:Array[Class[_]],path:RequestPathMetadata,val action:Any)
  extends DefaultInvocationMetadata(clazz,name,argumentTypes,path) {
}
