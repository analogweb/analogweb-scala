package org.analogweb.scala

import org.analogweb.RequestContext
import org.analogweb.RequestValueResolvers
import org.analogweb.InvocationMetadata
import org.analogweb.core.ParameterValueResolver
import collection.JavaConversions._

class Request(rc : RequestContext,rvr : RequestValueResolvers,im : InvocationMetadata) {

  def parameter(name: String):Option[String] = {
    parameters(name).headOption
  }

  def parameters(name: String) = {
    rc.getQueryParameters.getValues(name)
  }

  def header(name:String) = {
    headers(name).headOption
  }

  def headers(name:String) = {
    rc.getRequestHeaders.getValues(name)
  }

  def attribute(name:String) = {
    attributes(name).headOption
  }
  
  def attributes(name:String) = {
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]).resolveValue(rc,im,name,classOf[List[String]])
  }
}
