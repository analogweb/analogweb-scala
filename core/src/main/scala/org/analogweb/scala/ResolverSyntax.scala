package org.analogweb.scala

import scala.language.existentials
import scala.util.{Left, Right, Either}
import scala.reflect.ClassTag
import org.analogweb.{RequestValueResolver, TypeMapper}
import org.analogweb.core.{SpecificMediaTypeRequestValueResolver, UnsupportedMediaTypeException}

trait ResolverSyntax[R <: RequestValueResolver] {

  def request: Request

  def resolverContext: ResolverContext = NoResolverContext

  def requestValueResolver: Option[RequestValueResolver]

  def asOption[T](implicit ctag: ClassTag[T]): Option[T] =
    asOption("")(ctag)

  def asOption[T](name: String)(implicit ctag: ClassTag[T]): Option[T] =
    as(name)(ctag).right.toOption

  def as[T](implicit ctag: ClassTag[T]): Either[Throwable, T] =
    as("")(ctag)

  def as[T](name: String)(implicit ctag: ClassTag[T]): Either[Throwable, T] = {
    requestValueResolver
      .map {
        case scalaResolver: ScalaRequestValueResolver => {
          resolveInternalWithScalaResolver[T](name, scalaResolver) {
            _.resolve[T](
              request.context,
              request.metadata,
              name,
              ctag.runtimeClass
                .asInstanceOf[Class[T]]
            )(resolverContext)
          }
        }
        case javaResolver =>
          resolveInternal(name, javaResolver) {
            _.resolveValue(request.context, request.metadata, name, ctag.runtimeClass, Array())
          }.right.map(_.asInstanceOf[T])
      }
      .getOrElse(Left(ResolverNotFound(name)))
  }

  private[this] def resolveInternalWithScalaResolver[T](
      name: String,
      resolver: ScalaRequestValueResolver
  )(f: ScalaRequestValueResolver => Either[NoValuesResolved[T], T]): Either[Throwable, T] =
    verifyMediaType[ScalaRequestValueResolver](resolver).right.flatMap(f)

  private[this] def resolveInternal[T, RV <: RequestValueResolver](
      name: String,
      resolver: RV
  )(f: RV => AnyRef)(implicit ctag: ClassTag[T]) = {
    val mayBeVerified: Either[Throwable, RV] = verifyMediaType[RV](resolver)
    mayBeVerified.right
      .flatMap { verifiedResolver =>
        Option(f(verifiedResolver))
          .map {
            case Some(resolved) =>
              mappingToType(resolved)(ctag)
            case None =>
              Left(NoValuesResolved(name, resolver, ctag.runtimeClass))
            case resolved =>
              mappingToType(resolved)(ctag)
          }
          .getOrElse(Left(NoValuesResolved(name, resolver, ctag.runtimeClass)))
      }
  }

  private[this] def mappingToType[T](resolved: Any)(
      implicit ctag: ClassTag[T]): Either[ResolvedValueTypeMismatched[_], T] = {
    Option(
      request.converters
        .mapToType(classOf[TypeMapper], resolved, ctag.runtimeClass, Array()))
      .map {
        case Some(mapped) =>
          Right(
            mapped
              .asInstanceOf[T])
        case None => {
          if (ctag.runtimeClass
                .isInstance(resolved))
            Right(
              resolved
                .asInstanceOf[T])
          else
            Left(ResolvedValueTypeMismatched(resolved, ctag.runtimeClass))
        }
        case mapped =>
          Right(
            mapped
              .asInstanceOf[T])
      }
      .getOrElse {
        if (ctag.runtimeClass
              .isInstance(resolved))
          Right(
            resolved
              .asInstanceOf[T])
        else
          Left(ResolvedValueTypeMismatched(resolved, ctag.runtimeClass))
      }
  }

  private[this] def verifyMediaType[RV <: RequestValueResolver]
    : PartialFunction[RV, Either[Throwable, RV]] = {
    case resolver: SpecificMediaTypeRequestValueResolver =>
      request.contentTypeOption
        .map {
          case contentType
              if (!resolver
                .supports(contentType)) =>
            Left(new UnsupportedMediaTypeException(request.requestPath))
          case _ =>
            Right(
              resolver
                .asInstanceOf[RV])
        }
        .getOrElse(Right(resolver
          .asInstanceOf[RV]))
    case resolver =>
      Right(resolver)
  }

}

case class InstanceResolverSyntax[T <: RequestValueResolver](
    val resolver: T,
    override val request: Request,
    override val resolverContext: ResolverContext = NoResolverContext
) extends ResolverSyntax[T] {
  override val requestValueResolver: Option[RequestValueResolver] =
    Some(resolver)
}
