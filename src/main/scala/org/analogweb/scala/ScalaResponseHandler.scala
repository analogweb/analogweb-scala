package org.analogweb.scala

import scala.util._
import scala.concurrent._
import scala.concurrent.duration._
import org.analogweb.{ Renderable, ResponseFormatter, RequestContext, ResponseContext, WebApplicationException, InvocationMetadata }
import org.analogweb.core.{ DefaultResponseHandler, DefaultResponseResolver }
import org.analogweb.core.response.DefaultResponse
import scala.concurrent.ExecutionContext.Implicits.global

case class RenderableFuture(future: Future[Renderable]) extends DefaultResponse

class ScalaResponseResolver extends DefaultResponseResolver {

  override def resolve(result: Any, metadata: InvocationMetadata, request: RequestContext, response: ResponseContext) = {
    result match {
      case f: Future[_] => RenderableFuture(f.map { x => super.resolve(x, metadata, request, response) })
      case _            => super.resolve(result, metadata, request, response)
    }
  }
}

import java.util.concurrent.CountDownLatch

class ScalaResponseHandler extends DefaultResponseHandler {

  override def handleResult(result: Renderable, formatter: ResponseFormatter, request: RequestContext, response: ResponseContext) = {
    result match {
      case rf: RenderableFuture => {
        val latch = new CountDownLatch(1)
        rf.future.andThen { a =>
          a match {
            case Success(s) => {
              super.handleResult(s, formatter, request, response)
              latch.countDown()
            }
            case Failure(f) => throw f
          }
        }
        latch.await()
      }
      case _ => {
        super.handleResult(result, formatter, request, response)
      }
    }
  }
}
