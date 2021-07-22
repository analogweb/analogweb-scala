package org.analogweb.scala

import java.net.URI
import scala.concurrent.Future
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core._

@RunWith(classOf[JUnitRunner])
class RouteExtensionsSpec extends Specification with Mockito {

  trait mocks extends org.specs2.specification.Scope {
    val rc =
      mock[RequestContext]
    val rvr =
      mock[RequestValueResolvers]
    val im =
      mock[ScalaInvocationMetadata]
    val tc =
      mock[TypeMapperContext]
    val parameterResolver =
      mock[RequestValueResolver]
    val pathResolver =
      mock[RequestValueResolver]
    val r = new Request(rc, rvr, im, tc)
  }

  "Resolve with ParameterValueResolver" in new mocks {
    val qp = mock[Parameters]
    rc.getQueryParameters returns qp
    qp.getValues("foo") returns java.util.Collections
      .emptyList()
    qp.getValues("baa") returns java.util.Arrays
      .asList("baz")
    class A extends Resolvers with RouteExtensions {
      import analogweb._
      val route = get("/foo") { implicit r =>
        param("baa")
      }
    }
    new A().route
      .invoke(r) must_== "baz"
  }

  "Resolve with PathVariableValueResolver" in new mocks {
    val qp = mock[Parameters]
    rc.getQueryParameters returns qp
    qp.getValues("bar") returns java.util.Collections
      .emptyList()
    val mp = mock[MatrixParameters]
    rc.getMatrixParameters() returns mp
    mp.getValues("bar") returns java.util.Collections.emptyList()
    rc.getRequestMethod() returns "GET"
    val dp  = new DefaultRequestPath(URI.create("/"), URI.create("foo/baz"), "GET")
    val rpd = RequestPathDefinition.define("/", "foo/{bar}")
    im.getDefinedPath() returns rpd
    rc.getRequestPath() returns dp
    class A extends Resolvers with RouteExtensions {
      import analogweb._
      val route = get("/foo/{bar}") { implicit r =>
        param("bar")
      }
    }
    new A().route
      .invoke(r) must_== "baz"
  }

  "Not Resolved" in new mocks {
    val qp = mock[Parameters]
    rc.getQueryParameters returns qp
    qp.getValues("bar") returns java.util.Collections
      .emptyList()
    val mp = mock[MatrixParameters]
    rc.getMatrixParameters returns mp
    mp.getValues("bar") returns java.util.Collections.emptyList()
    rc.getRequestMethod() returns "GET"
    val dp  = new DefaultRequestPath(URI.create("/"), URI.create("foo"), "GET")
    val rpd = RequestPathDefinition.define("/", "foo")
    im.getDefinedPath() returns rpd
    rc.getRequestPath() returns dp
    class A extends Resolvers with RouteExtensions {
      import analogweb._
      val route = get("/foo") { implicit r =>
        param("bar")
      }
    }
    new A().route
      .invoke(r) must_== ""
  }

  "Passed With" in new mocks {
    class A extends Resolvers with RouteExtensions {
      import analogweb._
      val route = get("/foo") { r =>
        implicit val copied: Request =
          r.copy(passedWith = Map(("foo" -> "bar"), ("baz" -> true)))
        passedWith[String]("foo")
      }
    }
    new A().route
      .invoke(r) must_== Some("bar")
  }

  "Passed With Nothing" in new mocks {
    class A extends Resolvers with RouteExtensions {
      import analogweb._
      val route = get("/foo") { r =>
        implicit val copied: Request =
          r.copy(passedWith = Map(("foo" -> "bar"), ("baz" -> true)))
        passedWith[String]("bar")
      }
    }
    new A().route
      .invoke(r) must_== None
  }

  "Converting Future to Renderable" in new mocks {
    class A extends Resolvers with Responses with RouteExtensions {
      import analogweb._
      val route = get[RenderableFuture]("/foo") { r =>
        Future
          .successful(Ok(asText("hoge")))
          .asRenderable()
      }
    }
    new A().route
      .invoke(r)
      .isInstanceOf[RenderableFuture] must beTrue
  }
}
