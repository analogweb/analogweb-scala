package org.analogweb.scala

import org.analogweb.RequestContext
import org.analogweb.RequestValueResolvers
import org.analogweb.InvocationMetadata
import org.analogweb.core.ParameterValueResolver
import scala.collection.mutable.Buffer
import collection.JavaConverters._

class Request(rc : RequestContext,rvr : RequestValueResolvers,im : InvocationMetadata) {

  def parameter(name: String):Option[String] = {
    parameters(name).headOption
  }

  def parameters(name: String):Buffer[String] = {
    rc.getQueryParameters.getValues(name).asScala
  }

  def header(name:String):Option[String] = {
    headers(name).headOption
  }

  def headers(name:String):Buffer[String] = {
    rc.getRequestHeaders.getValues(name).asScala
  }

  def attribute(name:String) = {
    attributes(name).headOption
  }
  
  def attributes(name:String) = {
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]).resolveValue(rc,im,name,classOf[List[String]]).asInstanceOf[java.util.List[String]].asScala
  }
}
