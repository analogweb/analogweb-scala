package org.analogweb.scala

import scala.util.{ Try, Success, Failure }
import scala.concurrent.Future
import org.analogweb.{ Renderable, ResponseFormatterFinder, RequestContext, ResponseContext, InvocationMetadata, RenderableResolver, ExceptionHandler, Response }
import org.analogweb.core.{ DefaultResponseHandler, DefaultResponse }
import org.analogweb.scala.Execution.Implicits.defaultContext

class ScalaResponseHandler extends DefaultResponseHandler {

  override def handleResult(obj: Any, metadata: InvocationMetadata, resolver: RenderableResolver, request: RequestContext, response: ResponseContext, exh: ExceptionHandler, finder: ResponseFormatterFinder): Response = {
    val result = resolver.resolve(obj, metadata, request, response)
    result match {
      case r: RenderableFuture => {
        def futureResultHandler = { (request: RequestContext, response: ResponseContext) =>
          r.future.andThen { f =>
            f match {
              case Success(s) => {
                val committed = Try {
                  super.handleResult(s, metadata, resolver, request, response, exh, finder).commit(request, response)
                }
                committed match {
                  case Success(s) => response.ensure()
                  case Failure(f) =>
                    f match {
                      case e: Exception => {
                        super.handleResult(exh.handleException(e), metadata, resolver, request, response, exh, finder).commit(request, response)
                        response.ensure()
                      }
                      case t => throw t
                    }
                }
              }
              case Failure(f) => f match {
                case e: Exception => {
                  super.handleResult(exh.handleException(e), metadata, resolver, request, response, exh, finder).commit(request, response)
                  response.ensure()
                }
                case t => throw t
              }
              case _ => super.handleResult(result, metadata, resolver, request, response, exh, finder).commit(request, response)
            }
          }
        }
        new DefaultResponse {
          override def commit(request: RequestContext, response: ResponseContext) = {
            futureResultHandler(request, response)
          }
        }
      }
      case _ => {
        super.handleResult(result, metadata, resolver, request, response, exh, finder)
      }
    }
  }
}
