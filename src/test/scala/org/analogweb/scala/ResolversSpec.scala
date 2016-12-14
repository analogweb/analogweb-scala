package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.mockito.Matchers.{ eq => isEq }
import org.analogweb._
import org.analogweb.core._
import org.analogweb.scala._

case class B(val name: String)

@RunWith(classOf[JUnitRunner])
class ResolversSpec extends Specification with Mockito {

  trait mocks extends org.specs2.specification.Scope {
    val rc = mock[RequestContext]
    val rvr = mock[RequestValueResolvers]
    val im = mock[ScalaInvocationMetadata]
    val tc = mock[TypeMapperContext]
    val qp = mock[Parameters]
    val rh = mock[Headers]
    val resolver = mock[RequestValueResolver]
    val r = new Request(rc, rvr, im, tc)
  }

  "Resolve with ParameterValueResolver" in new mocks {
    rc.getQueryParameters returns qp
    qp.getValues("foo") returns java.util.Collections.emptyList()
    qp.getValues("baa") returns java.util.Arrays.asList("baz")
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]) returns new ParameterValueResolver()
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${parameter.of("baa").getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with PathVariableValueResolver" in new mocks {
    resolver.resolveValue(isEq(rc), isEq(im), isEq("baa"), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    rvr.findRequestValueResolver(classOf[PathVariableValueResolver]) returns resolver
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${path.of("baa").getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with CookieValueResolver" in new mocks {
    resolver.resolveValue(isEq(rc), isEq(im), isEq("baa"), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    rvr.findRequestValueResolver(classOf[CookieValueResolver]) returns resolver
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${cookie.of("baa").getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with RequestBodyValueResolver" in new mocks {
    resolver.resolveValue(isEq(rc), isEq(im), isEq(""), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    rvr.findRequestValueResolver(classOf[RequestBodyValueResolver]) returns resolver
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${body.as[java.lang.String].getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with XmlValueResolver" in new mocks {
    resolver.resolveValue(isEq(rc), isEq(im), isEq(""), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    rvr.findRequestValueResolver(classOf[XmlValueResolver]) returns resolver
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${xml.as[java.lang.String].getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with MultipartParameterResolver" in new mocks {
    resolver.resolveValue(isEq(rc), isEq(im), isEq("baa"), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    rvr.findRequestValueResolver(classOf[MultipartParameterResolver]) returns resolver
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${multipart.of("baa").getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with RequestContextValueResolver" in new mocks {
    resolver.resolveValue(isEq(rc), isEq(im), isEq(""), any[Class[_]], any[Array[java.lang.annotation.Annotation]]) returns "baz"
    rvr.findRequestValueResolver(classOf[RequestContextValueResolver]) returns resolver
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${context.as[java.lang.String].getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "baz"
  }

  "Resolve with MappingRequestValueResolver" in new mocks {
    class A extends Analogweb with Resolvers {
      val m: Request => B = { r => B(name = "foo") }
      get("/foo") { implicit r =>
        s"${m.name}"
      }
    }
    new A().routes(0).invoke(r) must_== "foo"
  }

}
