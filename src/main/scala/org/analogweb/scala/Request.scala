package org.analogweb.scala

import org.analogweb.RequestContext
import org.analogweb.RequestValueResolvers
import org.analogweb.InvocationMetadata
import org.analogweb.core.ParameterValueResolver
import org.analogweb.core.PathVariableValueResolver
import scala.collection.mutable.Buffer
import collection.JavaConverters._

class Request(rc: RequestContext, rvr: RequestValueResolvers, im: InvocationMetadata) {

  def parameter(name: String): Option[String] = parameters(name).headOption

  def parameters(name: String): Buffer[String] = rc.getQueryParameters.getValues(name).asScala

  def path(name: String): String = {
    Some(rvr.findRequestValueResolver(classOf[PathVariableValueResolver])).map { resolver =>
      resolver.resolveValue(rc, im, name, classOf[String]).asInstanceOf[String]
    }.getOrElse(throw new IllegalArgumentException)
  }

  def header(name: String): Option[String] = headers(name).headOption

  def headers(name: String): Buffer[String] = rc.getRequestHeaders.getValues(name).asScala

  def attribute(name: String) = attributes(name).headOption

  def attributes(name: String) = {
    Some(rvr.findRequestValueResolver(classOf[ParameterValueResolver])).map { resolver =>
      resolver.resolveValue(rc, im, name, classOf[List[String]]).asInstanceOf[java.util.List[String]].asScala
    }.getOrElse(throw new IllegalStateException)
  }

}
