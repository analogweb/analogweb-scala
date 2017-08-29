package org.analogweb.scala

import java.lang.annotation.Annotation
import org.analogweb._
import org.analogweb.core._

trait Resolvers {

  def parameter = classOf[ParameterValueResolver]

  def path = classOf[PathVariableValueResolver]

  def cookie = classOf[CookieValueResolver]

  def body = classOf[RequestBodyValueResolver]

  def xml = classOf[XmlValueResolver]

  def multipart = classOf[MultipartParameterResolver]

  def context = classOf[RequestContextValueResolver]

}

trait ResolverContext

object NoResolverContext extends ResolverContext

abstract class ScalaRequestValueResolver extends SpecificMediaTypeRequestValueResolver {

  def resolve[A](
    request:      RequestContext,
    metadata:     InvocationMetadata,
    key:          String,
    requiredType: Class[A]
  )(implicit context: ResolverContext): Either[NoValuesResolved[A], A]

  override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = {
    resolve(request, metadata, key, requiredType)(NoResolverContext)
  }

  override def supports(contentType: MediaType) = true

}
