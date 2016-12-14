package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.mockito.Matchers.{ eq => isEq }
import org.json4s.JsonDSL._
import org.analogweb._
import org.analogweb.core._
import org.analogweb.scala._
import org.analogweb.json4s._

@RunWith(classOf[JUnitRunner])
class ScalaJacksonJsonValueResolverSpec extends Specification with Mockito {

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

  "Resolve with ScalaJacksonJsonValueResolver" in new mocks {
    rc.getRequestBody() returns new java.io.ByteArrayInputStream("""{"name": "foo"}""".getBytes())
    rc.getContentType() returns org.analogweb.core.MediaTypes.APPLICATION_JSON_TYPE
    rvr.findRequestValueResolver(classOf[ScalaJacksonJsonValueResolver]) returns new ScalaJacksonJsonValueResolver()
    case class B(val name: String)
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        s"${json.as[org.analogweb.scala.B].map(x => x.name).getOrElse("a")}"
      }
    }
    new A().routes(0).invoke(r) must_== "foo"
  }

  "Resolve with ScalaJacksonJsonValueResolver as JValue" in new mocks {
    rc.getRequestBody() returns new java.io.ByteArrayInputStream("""{"name": "foo"}""".getBytes())
    rc.getContentType() returns org.analogweb.core.MediaTypes.APPLICATION_JSON_TYPE
    rvr.findRequestValueResolver(classOf[ScalaJacksonJsonValueResolver]) returns new ScalaJacksonJsonValueResolver()
    class A extends Analogweb with Resolvers {
      get("/foo") { implicit r =>
        json.as[org.json4s.JValue].map { x =>
          for {
            org.json4s.JString(name) <- x \ "name"
          } yield name
        }.headOption
      }
    }
    val result = new A().routes(0).invoke(r)
    result must_== Some(List("foo"))
  }
}
