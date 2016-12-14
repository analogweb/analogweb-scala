package org.analogweb.scala

import java.lang.annotation.Annotation
import org.analogweb.{ InvocationMetadata, MediaType, RequestContext, RequestValueResolver, TypeMapper }
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
