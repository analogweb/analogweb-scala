package org.analogweb.scala

import org.analogweb.RequestContext
import collection.JavaConversions._

class Request(rc : RequestContext) {

  def parameter(name: String):Option[String] = {
    parameters(name).headOption
  }

  def parameters(name: String) = {
    rc.getQueryParameters.getValues(name)
  }

}
