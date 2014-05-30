package org.analogweb.scala

import java.util
import collection.JavaConversions._
import org.analogweb._
import collection.mutable.ArrayBuffer

class ScalaInvocation(path: RequestPathMetadata, val route: Route,
    val rc: RequestContext, val rsc: ResponseContext,
    val tc: TypeMapperContext, val rvr: RequestValueResolvers,
    im: InvocationMetadata) extends Invocation with InvocationArguments {

  def invoke: Object = route.invoke(new Request(rc, rvr, im)).asInstanceOf[Object]

  def getInvocationInstance: Object = route

  def getInvocationArguments: InvocationArguments = this

  def replace(o: Object) = { /* nop */ }

  def putInvocationArgument(i: Int, o: Object) = { /* nop */ }

  def asList: util.List[Object] = ArrayBuffer.empty[Object]

}
