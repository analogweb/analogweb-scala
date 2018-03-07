package org.analogweb.scala

import java.lang.annotation.Annotation
import org.analogweb._
import org.analogweb.core._

trait Resolvers {

  lazy val parameter = new ParameterValueResolver

  lazy val path = new PathVariableValueResolver

  lazy val cookie = new CookieValueResolver

  lazy val body = new RequestBodyValueResolver

  lazy val multipart = new MultipartParameterResolver

  lazy val context = new RequestContextValueResolver

}

trait ResolverContext

object NoResolverContext extends ResolverContext

abstract class ScalaRequestValueResolver extends SpecificMediaTypeRequestValueResolver {

  def resolve[A](
      request: RequestContext,
      metadata: InvocationMetadata,
      key: String,
      requiredType: Class[A]
  )(implicit context: ResolverContext): Either[NoValuesResolved[A], A]

  override final def resolveValue(request: RequestContext,
                                  metadata: InvocationMetadata,
                                  key: String,
                                  requiredType: Class[_],
                                  annoattions: Array[Annotation]): AnyRef = {
    resolve(request, metadata, key, requiredType)(NoResolverContext)
  }

  override def supports(contentType: MediaType) =
    true

}
