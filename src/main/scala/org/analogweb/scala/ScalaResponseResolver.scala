package org.analogweb.scala

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.analogweb.{ Renderable, RequestContext, ResponseContext, InvocationMetadata }
import org.analogweb.core.DefaultResponseResolver
import org.analogweb.core.response.DefaultResponse

case class RenderableFuture(future: Future[Renderable]) extends DefaultResponse

class ScalaResponseResolver extends DefaultResponseResolver {

  override def resolve(result: Any, metadata: InvocationMetadata, request: RequestContext, response: ResponseContext) = {
    result match {
      case f: Future[_] => RenderableFuture(f.map { x => super.resolve(x, metadata, request, response) })
      case _            => super.resolve(result, metadata, request, response)
    }
  }

}
