package org.analogweb.scala

import scala.util.{Try, Success => USuccess, Failure}
import org.analogweb._
import org.analogweb.core.{DefaultResponseHandler, DefaultResponse, DefaultResponseEntity}
import org.analogweb.scala.Execution.Implicits.defaultContext
import org.analogweb.scala.utils.Implicits._

class ScalaResponseHandler extends DefaultResponseHandler {

  override def handleResult(
      obj: Any,
      metadata: InvocationMetadata,
      resolver: RenderableResolver,
      request: RequestContext,
      response: ResponseContext,
      exh: ExceptionHandler,
      finder: ResponseFormatterFinder
  ): Response = {

    val result = resolver.resolve(obj, metadata, request, response)
    def commit(a: Any) =
      super
        .handleResult(a, metadata, resolver, request, response, exh, finder)
        .commit(request, response)
    result match {
      case r: RenderableFuture => {
        def futureResultHandler = { (request: RequestContext, response: ResponseContext) =>
          r.future
            .andThen { future =>
              future match {
                case USuccess(s) =>
                  Try(commit(s)).eventually(
                    response
                      .ensure())
                case Failure(f) =>
                  f match {
                    case e: Exception =>
                      Try(commit(exh.handleException(e)))
                        .eventually(response
                          .ensure())
                    case t =>
                      throw t
                  }
              }
            }
        }
        new DefaultResponse(new DefaultResponseEntity("")) {
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
