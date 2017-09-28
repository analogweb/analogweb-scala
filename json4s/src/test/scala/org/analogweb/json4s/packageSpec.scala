package org.analogweb.json4s

import org.specs2.mutable.Specification
import org.specs2.control.LazyParameter
import org.specs2.mock.Mockito
import org.mockito.Matchers.{ eq => isEq }
import org.json4s._, JsonDSL._
import org.analogweb._, core._, core.DefaultReadableBuffer._, core.DefaultWritableBuffer._, core.response._, scala._, scala.Responses._

case class Person(val name: String)

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
    rc.getRequestBody() returns readBuffer(new java.io.ByteArrayInputStream("""{"name": "foo"}""".getBytes()))
    rc.getContentType() returns org.analogweb.core.MediaTypes.APPLICATION_JSON_TYPE
    rvr.findRequestValueResolver(classOf[Json4sJsonValueResolver]) returns new Json4sJsonValueResolver()
    import analogweb._, json4s._
    val aRoute = get("/foo") { implicit r =>
      implicit val f: Formats = DefaultFormats
      json.as[Person].fold(l => "left", r => r.name)
    }
    aRoute.invoke(r) must_== "foo"
  }

  "Resolve with ScalaJacksonJsonValueResolver as JValue" in new mocks {
    rc.getRequestBody() returns readBuffer(new java.io.ByteArrayInputStream("""{"name": "foo"}""".getBytes()))
    rc.getContentType() returns org.analogweb.core.MediaTypes.APPLICATION_JSON_TYPE
    rvr.findRequestValueResolver(classOf[Json4sJsonValueResolver]) returns new Json4sJsonValueResolver()
    import analogweb._, json4s._
    val aRoute = get("/foo") { implicit r =>
      implicit val f: Formats = DefaultFormats
      json.as[org.json4s.JValue].fold(l => "left", x => {
        for {
          org.json4s.JString(name) <- x \ "name"
        } yield name
      }.head)
    }
    val result = aRoute.invoke(r)
    result must_== "foo"
  }

  "Responses" should {
    "Expected instance" in new mocks {
      import analogweb.json4s._
      val badRequestAsJson = BadRequest(asJson("foo"))
      badRequestAsJson.getStatusCode === 400
      badRequestAsJson.getRenderable.isInstanceOf[Json] === true
      val badRequest = BadRequest
      badRequest.getStatusCode === 400
      val notFoundAsJson = NotFound(asJson(asJson("a")))
    }
    "Expected instance with another resource type" in new mocks {
      import analogweb.json4s._
      val buffer = readBuffer(new java.io.ByteArrayInputStream("foo".getBytes()))
      val badRequestAsJson = BadRequest(asJson(buffer))
      badRequestAsJson.getStatusCode === 400
      badRequestAsJson.getRenderable.isInstanceOf[Json] === true
      val bytes = new java.io.ByteArrayInputStream("bar".getBytes())
      val badRequest = BadRequest
      badRequest.getStatusCode === 400
      val notFoundAsJson = NotFound(asJson(asJson("bar")))
    }
  }

  case class ScalaJsonFormatterA(id: String)

  "ScalaJsonFormatter" should {
    "be render" in {
      val formatter = new Json4sJsonFormatter

      val req = mock[RequestContext]
      val res = mock[ResponseContext]
      val c = new ScalaJsonFormatterA("snowgooseyk")
      val out = new java.io.ByteArrayOutputStream()
      val buffer = writeBuffer(out)
      formatter.formatAndWriteInto(req, res, "UTF-8", c).writeInto(buffer)
      new String(out.toByteArray()) === """{"id":"snowgooseyk"}"""
    }
    "be render with JValue" in {
      val formatter = new Json4sJsonFormatter

      val req = mock[RequestContext]
      val res = mock[ResponseContext]
      val c = ("name" -> "snowgooseyk") ~ ("email" -> "snowgoose.yk@gmail.com")
      val out = new java.io.ByteArrayOutputStream()
      val buffer = writeBuffer(out)
      formatter.formatAndWriteInto(req, res, "UTF-8", c).writeInto(buffer)
      new String(out.toByteArray()) === """{"name":"snowgooseyk","email":"snowgoose.yk@gmail.com"}"""
    }
    "be render with Tuple" in {
      val formatter = new Json4sJsonFormatter

      val req = mock[RequestContext]
      val res = mock[ResponseContext]
      val c = ("id" -> "snowgooseyk")
      val out = new java.io.ByteArrayOutputStream()
      val buffer = writeBuffer(out)
      formatter.formatAndWriteInto(req, res, "UTF-8", c).writeInto(buffer)
      new String(out.toByteArray()) === """{"id":"snowgooseyk"}"""
    }
  }

}
