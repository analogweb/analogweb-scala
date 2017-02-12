package org.analogweb.scala

import scala.concurrent.Future
import scala.concurrent.duration._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._, core._, core.response.Text, scala.Execution.Implicits._

@RunWith(classOf[JUnitRunner])
class ScalaResponseHandlerSpec extends Specification with Mockito {

  val handler = new ScalaResponseHandler

  "ScalaResponseHandler" should {
    "resolve simple result" in new Fixture {
      val txt = Text.`with`("hello!")
      formatter.resolve(txt, metadata, request, response) returns txt
      val actual = handler.handleResult(txt, metadata, formatter, request, response, exph, finder)
      actual.commit(request, response)
      response.writtenInBytes === "hello!"
    }
    "resolve Future successfully" in new Fixture {
      val txt = Text.`with`("yay!")
      val ft = Future(txt)
      val future = RenderableFuture(ft)
      formatter.resolve(future, metadata, request, response) returns future
      formatter.resolve(txt, metadata, request, response) returns txt
      val actual = handler.handleResult(future, metadata, formatter, request, response, exph, finder)
      actual.commit(request, response)
      //XXX improve test stability.
      Thread sleep 1000
      actual.isInstanceOf[DefaultResponse] === true
      response.writtenInBytes === "yay!"
    }
    "resolve Future failure" in new Fixture {
      val exp = new RuntimeException("oops")
      val ft = Future(throw exp)
      val future = RenderableFuture(ft)
      formatter.resolve(future, metadata, request, response) returns future
      val txt = Text.`with`("oops!")
      exph.handleException(exp) returns txt
      formatter.resolve(txt, metadata, request, response) returns txt
      val actual = handler.handleResult(future, metadata, formatter, request, response, exph, finder)
      actual.commit(request, response)
      //XXX improve test stability.
      Thread sleep 1000
      actual.isInstanceOf[DefaultResponse] === true
      response.writtenInBytes === "oops!"
    }
  }

  trait Fixture extends Before {
    def before = ()
    val metadata = mock[InvocationMetadata]
    val formatter = mock[RenderableResolver]
    val request = mock[RequestContext]
    val exph = mock[ExceptionHandler]
    val finder = mock[ResponseFormatterFinder]
    val response = new StubResponseContext
  }

  class StubResponseContext extends AbstractResponseContext {
    val bytes = new java.io.ByteArrayOutputStream
    def writtenInBytes = new String(bytes.toByteArray)
    val buffer = DefaultWritableBuffer.writeBuffer(bytes)
    def commit(req: RequestContext, res: Response) = res.getEntity.writeInto(buffer)
  }

}

