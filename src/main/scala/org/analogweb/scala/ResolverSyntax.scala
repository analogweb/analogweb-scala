package org.analogweb.scala

import scala.util._
import scala.reflect.ClassTag
import org.analogweb._
import org.analogweb.core._

trait ResolverSyntax[T <: RequestValueResolver] {

  def resolverType: Class[T]
  def request: Request
  def get(name: String)(implicit resolverContext: ResolverContext = NoResolverContext): String = of(name).getOrElse("")
  def of(name: String)(implicit resolverContext: ResolverContext = NoResolverContext): Option[String] = as[String](name)

  def as[T](implicit ctag: ClassTag[T]): Option[T] = as("")(ctag)

  def as[T](name: String)(implicit ctag: ClassTag[T], resolverContext: ResolverContext = NoResolverContext): Option[T] = asEach[T](name).toOption

  def asEach[T](name: String)(implicit ctag: ClassTag[T], resolverContext: ResolverContext = NoResolverContext): Try[T] = {
    Option(request.resolvers.findRequestValueResolver(resolverType)).map { resolver =>
      resolveInternal(name, resolver)
    }.getOrElse(Failure(ResolverNotFound(name)))
  }

  private[this] def resolveInternal[T](name: String, resolver: RequestValueResolver)(implicit ctag: ClassTag[T]) = {
    val verified = verifyMediaType(resolver)
    verified.flatMap { verifiedResolver =>
      Option(verifiedResolver.resolveValue(request.context, request.metadata, name, ctag.runtimeClass, Array())).map {
        case Some(resolved) => mappingToType(resolved)(ctag)
        case None           => Failure(NoValuesResolved(name, resolver, ctag.runtimeClass))
        case resolved       => mappingToType(resolved)(ctag)
      }.getOrElse(Failure(NoValuesResolved(name, resolver, ctag.runtimeClass)))
    }
  }

  private[this] def mappingToType[T](resolved: Any)(implicit ctag: ClassTag[T]) = {
    Option(request.converters.mapToType(classOf[TypeMapper], resolved, ctag.runtimeClass, Array())).map {
      case Some(mapped) => Success(mapped.asInstanceOf[T])
      case None => {
        if (ctag.runtimeClass.isInstance(resolved))
          Success(resolved.asInstanceOf[T])
        else
          Failure(ResolvedValueTypeMismatched(resolved, ctag.runtimeClass))
      }
      case mapped => Success(mapped.asInstanceOf[T])
    }.getOrElse {
      if (ctag.runtimeClass.isInstance(resolved))
        Success(resolved.asInstanceOf[T])
      else
        Failure(ResolvedValueTypeMismatched(resolved, ctag.runtimeClass))
    }
  }

  private[this] def verifyMediaType: PartialFunction[RequestValueResolver, Try[RequestValueResolver]] = {
    case resolver: SpecificMediaTypeRequestValueResolver => request.contentTypeOption.map {
      case contentType if (!resolver.supports(contentType)) => Failure(new UnsupportedMediaTypeException(request.requestPath))
      case _ => Success(resolver)
    }.getOrElse(Success(resolver))
    case resolver => Success(resolver)
  }

}

case class DefaultResolverSyntax[T <: RequestValueResolver](
  override val resolverType: Class[T],
  override val request:      Request
) extends ResolverSyntax[T]
