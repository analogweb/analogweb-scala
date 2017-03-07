package org.analogweb.scala

import scala.util._
import scala.reflect.ClassTag
import org.analogweb._
import org.analogweb.core._

trait ResolverSyntax[R <: RequestValueResolver] {

  def resolverType: Class[R]

  def request: Request

  def get(name: String): String = of(name).getOrElse("")

  def of(name: String): Option[String] = asTry[String](name, NoResolverContext).toOption

  def as[T](implicit ctag: ClassTag[T]): Either[Throwable, T] = asEach("", NoResolverContext)(ctag)

  def as[T](resolverContext: ResolverContext)(implicit ctag: ClassTag[T]): Either[Throwable, T] = asEach("", resolverContext)(ctag)

  def as[T](name: String, resolverContext: ResolverContext = NoResolverContext)(implicit ctag: ClassTag[T]): Either[Throwable, T] = asEach[T](name, resolverContext)

  def asEach[T](name: String, resolverContext: ResolverContext)(implicit ctag: ClassTag[T]): Either[Throwable, T] = {
    asTry(name, resolverContext)(ctag).map(Right(_)).recover { case t => Left(t) }.get
  }

  def asTry[T](name: String, resolverContext: ResolverContext)(implicit ctag: ClassTag[T]): Try[T] = {
    Option(request.resolvers.findRequestValueResolver(resolverType)).map {
      case scalaResolver: ScalaRequestValueResolver => resolveInternal[T, ScalaRequestValueResolver](name, scalaResolver) {
        _.resolve(request.context, request.metadata, name, ctag.runtimeClass)(resolverContext)
      }
      case resolver => resolveInternal(name, resolver) {
        _.resolveValue(request.context, request.metadata, name, ctag.runtimeClass, Array())
      }
    }.getOrElse(Failure(ResolverNotFound(name)))
  }

  private[this] def resolveInternal[T, RV <: RequestValueResolver](name: String, resolver: RV)(f: RV => AnyRef)(implicit ctag: ClassTag[T]) = {
    val mayBeVerified: Try[RV] = verifyMediaType[RV](resolver)
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

  private[this] def verifyMediaType[RV <: RequestValueResolver]: PartialFunction[RV, Try[RV]] = {
    case resolver: SpecificMediaTypeRequestValueResolver => request.contentTypeOption.map {
      case contentType if (!resolver.supports(contentType)) => Failure(new UnsupportedMediaTypeException(request.requestPath))
      case _ => Success(resolver.asInstanceOf[RV])
    }.getOrElse(Success(resolver.asInstanceOf[RV]))
    case resolver => Success(resolver)
  }

}

case class DefaultResolverSyntax[T <: RequestValueResolver](
  override val resolverType: Class[T],
  override val request:      Request
) extends ResolverSyntax[T]
