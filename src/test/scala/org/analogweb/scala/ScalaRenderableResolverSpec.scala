package org.analogweb.scala

import scala.concurrent.Future
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core.response.Text

@RunWith(classOf[JUnitRunner])
class ScalaRenderableResolverSpec extends Specification with Mockito {

  val resolver = new ScalaRenderableResolver

  "ScalaResponseResolver" should {
    "resolve Future" in new Fixture {
      val future = Future.successful("yay!")
      val actual = resolver.resolve(future, metadata, request, response)
      actual.isInstanceOf[RenderableFuture] === true
    }
    "resolve nested Future" in new Fixture {
      val future = Future.successful(Future.successful("yay!"))
      val actual = resolver.resolve(future, metadata, request, response)
      actual.isInstanceOf[RenderableFuture] === true
    }
    "resolve Option" in new Fixture {
      val option = Option("yay!")
      val actual = resolver.resolve(option, metadata, request, response)
      actual.isInstanceOf[Text] === true
    }
    "resolve None as NotFound" in new Fixture {
      val option = None
      val actual = resolver.resolve(option, metadata, request, response)
      actual === Responses.NotFound
    }
    "resolve Renderable" in new Fixture {
      val renderable = mock[Renderable]
      val actual = resolver.resolve(renderable, metadata, request, response)
      actual === renderable
    }
  }

  trait Fixture extends Before {
    def before = ()
    val metadata = mock[InvocationMetadata]
    val request = mock[RequestContext]
    val response = mock[ResponseContext]
  }

}

