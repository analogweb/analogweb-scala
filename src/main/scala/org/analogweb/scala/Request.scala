package org.analogweb.scala

import org.analogweb.RequestContext
import org.analogweb.RequestPathMetadata
import org.analogweb.RequestValueResolvers
import org.analogweb.InvocationMetadata
import org.analogweb.TypeMapperContext
import org.analogweb.MediaType
import org.analogweb.core.ParameterValueResolver
import org.analogweb.core.PathVariableValueResolver
import scala.collection.mutable.Buffer
import collection.JavaConverters._

class Request(val context: RequestContext, val resolvers: RequestValueResolvers, val metadata: InvocationMetadata, val converters: TypeMapperContext) {

  def query(name: String): Option[String] = queries(name).headOption

  def queries(name: String): Buffer[String] = context.getQueryParameters.getValues(name).asScala

  def header(name: String): Option[String] = headers(name).headOption

  def headers(name: String): Buffer[String] = context.getRequestHeaders.getValues(name).asScala

  def contentType: MediaType = context.getContentType()

  def contentTypeOption: Option[MediaType] = Option(context.getContentType())

  def content: Option[String] = contentTypeOption.map(_.toString)

  def requestPath: RequestPathMetadata = context.getRequestPath()
}
