package org.analogweb.scala

import scala.util.{ Success, Failure }
import scala.concurrent.Future
import org.analogweb.{ Renderable, ResponseFormatter, RequestContext, ResponseContext }
import org.analogweb.core.{ DefaultResponseHandler, DefaultResponse }
import org.analogweb.scala.Execution.Implicits.defaultContext

class ScalaResponseHandler extends DefaultResponseHandler {

  override def handleResult(result: Renderable, formatter: ResponseFormatter, request: RequestContext, response: ResponseContext) = {
    result match {
      case r: RenderableFuture => {
        def futureResultHandler = { (request: RequestContext, response: ResponseContext) =>
          r.future.andThen { f =>
            f match {
              case Success(s) => {
                try {
                  super.handleResult(s, formatter, request, response).commit(request, response)
                } finally {
                  response.ensure()
                }
              }
              case Failure(f) => throw f
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
        super.handleResult(result, formatter, request, response)
      }
    }
  }
}
