package org.analogweb.scala

import java.lang.annotation.Annotation
import org.analogweb._
import org.analogweb.core._

trait Resolvers {

  protected def parameter = classOf[ParameterValueResolver]

  protected def path = classOf[PathVariableValueResolver]

  protected def cookie = classOf[CookieValueResolver]

  protected def body = classOf[RequestBodyValueResolver]

  protected def xml = classOf[XmlValueResolver]

  protected def multipart = classOf[MultipartParameterResolver]

  protected def context = classOf[RequestContextValueResolver]

}

trait ResolverContext

object NoResolverContext extends ResolverContext

abstract class ScalaRequestValueResolver extends SpecificMediaTypeRequestValueResolver {

  def resolve(
    request:      RequestContext,
    metadata:     InvocationMetadata,
    key:          String,
    requiredType: Class[_]
  )(implicit context: ResolverContext): AnyRef

  override final def resolveValue(request: RequestContext, metadata: InvocationMetadata, key: String, requiredType: Class[_], annoattions: Array[Annotation]): AnyRef = {
    resolve(request, metadata, key, requiredType)(NoResolverContext)
  }

  override def supports(contentType: MediaType) = true

}
