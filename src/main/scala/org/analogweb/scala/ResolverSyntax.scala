package org.analogweb.scala

import scala.util._
import scala.reflect.ClassTag
import org.analogweb._
import org.analogweb.core._

trait ResolverSyntax[T <: RequestValueResolver] {

  def resolverType: Class[T]

  def request: Request

  def get(name: String): String = of(name).getOrElse("")

  def of(name: String): Option[String] = as[String](name, NoResolverContext)

  def as[T](implicit ctag: ClassTag[T]): Option[T] = asEach("", NoResolverContext)(ctag).toOption

  def as[T](resolverContext: ResolverContext)(implicit ctag: ClassTag[T]): Option[T] = asEach("", resolverContext)(ctag).toOption

  def as[T](name: String, resolverContext: ResolverContext = NoResolverContext)(implicit ctag: ClassTag[T]): Option[T] = asEach[T](name, resolverContext).toOption

  def asEach[T](name: String, resolverContext: ResolverContext)(implicit ctag: ClassTag[T]): Try[T] = {
    Option(request.resolvers.findRequestValueResolver(resolverType)).map {
      case scalaResolver: ScalaRequestValueResolver => resolveInternal[T, ScalaRequestValueResolver](name, scalaResolver) {
        _.resolve(request.context, request.metadata, name, ctag.runtimeClass)(resolverContext)
      }
      case resolver => resolveInternal(name, resolver) {
        _.resolveValue(request.context, request.metadata, name, ctag.runtimeClass, Array())
      }
    }.getOrElse(Failure(ResolverNotFound(name)))
  }

  private[this] def resolveInternal[T, R <: RequestValueResolver](name: String, resolver: R)(f: R => AnyRef)(implicit ctag: ClassTag[T]) = {
    val mayBeVerified: Try[R] = verifyMediaType[R](resolver)
    mayBeVerified.flatMap { verifiedResolver =>
      Option(f(verifiedResolver)).map {
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

  private[this] def verifyMediaType[R <: RequestValueResolver]: PartialFunction[R, Try[R]] = {
    case resolver: SpecificMediaTypeRequestValueResolver => request.contentTypeOption.map {
      case contentType if (!resolver.supports(contentType)) => Failure(new UnsupportedMediaTypeException(request.requestPath))
      case _ => Success(resolver.asInstanceOf[R])
    }.getOrElse(Success(resolver.asInstanceOf[R]))
    case resolver => Success(resolver)
  }

}

case class DefaultResolverSyntax[T <: RequestValueResolver](
  override val resolverType: Class[T],
  override val request:      Request
) extends ResolverSyntax[T]
