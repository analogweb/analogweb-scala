package org.analogweb.scala

import scala.concurrent.Future
import org.analogweb.{ Renderable, RequestContext, ResponseContext, InvocationMetadata }
import org.analogweb.core.DefaultRenderableResolver
import org.analogweb.core.response.DefaultRenderable
import org.analogweb.scala.Execution.Implicits.defaultContext

case class RenderableFuture(future: Future[Renderable]) extends DefaultRenderable

class ScalaRenderableResolver extends DefaultRenderableResolver with Responses {

  override def resolve(result: Any, metadata: InvocationMetadata, request: RequestContext, response: ResponseContext) = {
    result match {
      case f: Future[_]  => RenderableFuture(f.map(this.resolve(_, metadata, request, response)))
      case o: Option[_]  => o.map(this.resolve(_, metadata, request, response)).getOrElse(NotFound)
      case r: Renderable => r
      case _             => super.resolve(result, metadata, request, response)
    }
  }

}
