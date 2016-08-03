package org.analogweb.scala

import org.analogweb.{ RequestContext, RequestPathMetadata, RequestValueResolvers, InvocationMetadata, TypeMapperContext, MediaType }
import org.analogweb.core.{ ParameterValueResolver, PathVariableValueResolver }
import scala.collection.mutable.Buffer
import scala.collection.convert.decorateAsScala._

case class Request(
    val context:                   RequestContext,
    val resolvers:                 RequestValueResolvers,
    val metadata:                  InvocationMetadata,
    val converters:                TypeMapperContext,
    private[scala] val passedWith: Map[String, Any]      = Map.empty
) {

  def query(name: String, defaultValue: String = ""): String = queryOption(name).getOrElse(defaultValue)

  def queryOption(name: String): Option[String] = queries(name).headOption

  def queries(name: String): Buffer[String] = context.getQueryParameters.getValues(name).asScala

  def header(name: String, defaultValue: String = ""): String = headerOption(name).getOrElse(defaultValue)

  def headerOption(name: String): Option[String] = headers(name).headOption

  def headers(name: String): Buffer[String] = context.getRequestHeaders.getValues(name).asScala

  def contentType: MediaType = context.getContentType()

  def contentTypeOption: Option[MediaType] = Option(context.getContentType())

  def content: Option[String] = contentTypeOption.map(_.toString)

  def requestPath: RequestPathMetadata = context.getRequestPath()
}
