package org.analogweb.scala

import java.util
import collection.JavaConversions._
import org.analogweb._
import collection.mutable.ArrayBuffer

class ScalaInvocation(path:RequestPathMetadata,action:Some[_]) extends Invocation with InvocationArguments {

  def invoke:Object = {
    println("lift action")
    action.getOrElse("some").asInstanceOf[Object]
  }

  def getInvocationInstance:Object = {
    action 
  }

  def getInvocationArguments:InvocationArguments = {
    this
  }

  def replace(o:Object) = {
    // nop
  }

  def putInvocationArgument(i:Int , o:Object) = {
         // nop
  }

  def asList:util.List[Object] = {
    ArrayBuffer.empty[Object]
  }

}
