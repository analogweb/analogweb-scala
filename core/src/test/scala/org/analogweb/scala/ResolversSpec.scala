package org.analogweb.scala

import java.net.URI
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._, core._

case class B(val name: String)

@RunWith(classOf[JUnitRunner])
class ResolversSpec extends Specification with Mockito {

  trait mocks extends org.specs2.specification.Scope {
    val rc =
      mock[RequestContext]
    val rvr =
      mock[RequestValueResolvers]
    val im =
      mock[ScalaInvocationMetadata]
    val tc =
      mock[TypeMapperContext]
    val qp =
      mock[Parameters]
    val rh =
      mock[Headers]
    val resolver =
      mock[RequestValueResolver]
    val r = new Request(rc, rvr, im, tc)
  }

  "Resolve with ParameterValueResolver" in new mocks {
    rc.getQueryParameters returns qp
    qp.getValues("foo") returns java.util.Collections
      .emptyList()
    qp.getValues("baa") returns java.util.Arrays
      .asList("baz")
    class A extends Resolvers {
      import analogweb._
      val route = get("/foo") { implicit r =>
        s"${parameter.asOption[String]("baa").getOrElse("a")}"
      }
    }
    new A().route
      .invoke(r) must_== "baz"
  }

  "Resolve with PathVariableValueResolver" in new mocks {
    val dp = new DefaultRequestPath(URI.create("/"),URI.create("foo/baz"),"GET")
    val rpd = RequestPathDefinition.define("/","foo/{bar}")
    im.getDefinedPath() returns rpd
    rc.getRequestPath() returns dp
    class A extends Resolvers {
      import analogweb._
      val route = get("/foo/{bar}") { implicit r =>
        s"${path.asOption[String]("bar").getOrElse("a")}"
      }
    }
    new A().route
      .invoke(r) must_== "baz"
  }

  "Resolve with CookieValueResolver" in new mocks {
    val cks = mock[Cookies]
    val ck = mock[Cookies.Cookie]
    ck.getValue returns "baz"
    rc.getCookies returns cks
    cks.getCookie("bar") returns ck
    class A extends Resolvers {
      import analogweb._
      val route = get("/foo") { implicit r =>
        s"${cookie.asOption[String]("bar").getOrElse("a")}"
      }
    }
    new A().route
      .invoke(r) must_== "baz"
  }

  "Resolve with RequestBodyValueResolver" in new mocks {
    rc.getRequestBody() returns DefaultReadableBuffer.readBuffer("baz".getBytes())
    class A extends Resolvers {
      import analogweb._
      val route = get("/foo") { implicit r =>
        s"${body.as[java.lang.String].right.toOption.getOrElse("a")}"
      }
    }
    new A().route
      .invoke(r) must_== "baz"
  }

  "Resolve with MappingRequestValueResolver" in new mocks {
    class A extends Resolvers {
      import analogweb._
      val m: Request => B = { r =>
        B(name = "foo")
      }
      val route = get("/foo") { implicit r =>
        s"${m.name}"
      }
    }
    new A().route
      .invoke(r) must_== "foo"
  }

}
