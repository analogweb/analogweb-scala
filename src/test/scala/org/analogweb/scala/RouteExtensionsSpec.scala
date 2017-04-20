package org.analogweb.scala

import scala.concurrent.Future
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.mockito.Matchers.{ eq => isEq }
import org.analogweb._
import org.analogweb.core._
import org.analogweb.scala._

@RunWith(classOf[JUnitRunner])
class RouteExtensionsSpec extends Specification with Mockito {

  trait mocks extends org.specs2.specification.Scope {
    val rc = mock[RequestContext]
    val rvr = mock[RequestValueResolvers]
    val im = mock[ScalaInvocationMetadata]
    val tc = mock[TypeMapperContext]
    val parameterResolver = mock[RequestValueResolver]
    val pathResolver = mock[RequestValueResolver]
    val r = new Request(rc, rvr, im, tc)
  }

  "Resolve with ParameterValueResolver" in new mocks {
    pathResolver.resolveValue(rc, im, "baa", classOf[String], Array()) returns null
    parameterResolver.resolveValue(rc, im, "baa", classOf[String], Array()) returns "baz"
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]) returns parameterResolver
    rvr.findRequestValueResolver(classOf[PathVariableValueResolver]) returns pathResolver
    class A extends Analogweb with Resolvers with RouteExtensions {
      get("/foo") { implicit r =>
        param("baa")
      }
    }
    new A().routeList(0).invoke(r) must_== "baz"
  }

  "Resolve with PathVariableValueResolver" in new mocks {
    pathResolver.resolveValue(isEq(rc), isEq(im), isEq("baa"), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    parameterResolver.resolveValue(isEq(rc), isEq(im), isEq("baa"), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns null
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]) returns parameterResolver
    rvr.findRequestValueResolver(classOf[PathVariableValueResolver]) returns pathResolver
    class A extends Analogweb with Resolvers with RouteExtensions {
      get("/foo") { implicit r =>
        param("baa")
      }
    }
    new A().routeList(0).invoke(r) must_== "baz"
  }

  "Not Resolved" in new mocks {
    pathResolver.resolveValue(rc, im, "baa", classOf[String], Array()) returns null
    parameterResolver.resolveValue(rc, im, "baa", classOf[String], Array()) returns null
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]) returns parameterResolver
    rvr.findRequestValueResolver(classOf[PathVariableValueResolver]) returns pathResolver
    class A extends Analogweb with Resolvers with RouteExtensions {
      get("/foo") { implicit r =>
        param("baa")
      }
    }
    new A().routeList(0).invoke(r) must_== ""
  }

  "Passed With" in new mocks {
    class A extends Analogweb with Resolvers with RouteExtensions {
      get("/foo") { r =>
        implicit val copied = r.copy(passedWith = Map("foo" -> "bar", "baz" -> true))
        passedWith[String]("foo")
      }
    }
    new A().routeList(0).invoke(r) must_== Some("bar")
  }

  "Passed With Nothing" in new mocks {
    class A extends Analogweb with Resolvers with RouteExtensions {
      get("/foo") { r =>
        implicit val copied = r.copy(passedWith = Map("foo" -> "bar", "baz" -> true))
        passedWith[String]("bar")
      }
    }
    new A().routeList(0).invoke(r) must_== None
  }

  "Converting Future to Renderable" in new mocks {
    class A extends StrictRouteDef with Resolvers with Responses with RouteExtensions {
      get("/foo") { r =>
        Future.successful(Ok(asText("hoge"))).asRenderable
      }
    }
    new A().routeList(0).invoke(r).isInstanceOf[RenderableFuture] must beTrue
  }
}
