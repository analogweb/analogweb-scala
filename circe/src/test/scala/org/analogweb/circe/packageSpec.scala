package org.analogweb.circe

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import org.analogweb._, core.DefaultReadableBuffer._, core.response._, scala._

case class Person(val name: String)

@RunWith(classOf[JUnitRunner])
class CirceValueResolverSpec extends Specification with Mockito {

  //trait mocks extends org.specs2.specification.Scope {
  trait mocks extends Scope {
    val rc       = mock[RequestContext]
    val rvr      = mock[RequestValueResolvers]
    val im       = mock[ScalaInvocationMetadata]
    val tc       = mock[TypeMapperContext]
    val qp       = mock[Parameters]
    val rh       = mock[Headers]
    val resolver = mock[RequestValueResolver]
    val r        = new Request(rc, rvr, im, tc)
  }

  "Resolve with CirceValueResolver" in new mocks {
    rc.getRequestBody() returns readBuffer(
      new java.io.ByteArrayInputStream("""{"name": "foo"}""".getBytes()))
    rc.getContentType() returns org.analogweb.core.MediaTypes.APPLICATION_JSON_TYPE
    rvr.findRequestValueResolver(classOf[CirceJsonValueResolver]) returns new CirceJsonValueResolver()
    import analogweb._, circe._, io.circe._, generic.semiauto._
    val aRoute = get("/foo") { implicit r =>
      implicit val personDecoder: Decoder[Person] = deriveDecoder[Person]
      json.as[Person].fold(l => "left", r => r.name)
    }
    aRoute.invoke(r) must_== "foo"
  }

  "Resolve with CirceValueResolver as Json" in new mocks {
    rc.getRequestBody() returns readBuffer(
      new java.io.ByteArrayInputStream("""{"name": "foo"}""".getBytes()))
    rc.getContentType() returns org.analogweb.core.MediaTypes.APPLICATION_JSON_TYPE
    rvr.findRequestValueResolver(classOf[CirceJsonValueResolver]) returns new CirceJsonValueResolver()
    import analogweb._, circe._, io.circe._
    val aRoute = get("/foo") { implicit r =>
      val maybeName = for {
        //FIXME: Cannot resolve InstanceResolverSyntax automatically on dotty
        js   <- asCirceJsonValueResolverSyntax[Json](json).as[Json].right
        name <- js.hcursor.get[String]("name").right
      } yield name
      maybeName.fold(l => "left", r => r)
    }
    val result = aRoute.invoke(r)
    result must_== "foo"
  }

  "Responses" should {
    "Expected instance" in new mocks {
      import analogweb._, circe._
      val badRequestAsJson = BadRequest(asJson("foo"))
      badRequestAsJson.getStatusCode === 400
      badRequestAsJson.getRenderable.isInstanceOf[Json] === true
      val badRequest = BadRequest
      badRequest.getStatusCode === 400
      val notFoundAsJson = NotFound(asJson("a"))
      notFoundAsJson.getStatusCode === 404
    }
  }

  case class ScalaJsonFormatterA(id: String, email: String)

  "ScalaJsonFormatter" should {
    "be render" in {
      import io.circe.generic.semiauto._
      val formatter = new CirceJsonFormatter

      val req     = mock[RequestContext]
      val res     = mock[ResponseContext]
      val encoder = deriveEncoder[ScalaJsonFormatterA]
      val c       = ScalaJsonFormatterA("y2k2mt", "y2k2mt@xxx.com")
      val bytes = formatter
        .formatAndWriteInto(req, res, "UTF-8", (c, encoder))
        .entity()
        .asInstanceOf[Array[Byte]]
      new String(bytes) === """{"id":"y2k2mt","email":"y2k2mt@xxx.com"}"""
    }
    "be render with specific encoder" in {
      import io.circe.Encoder
      val formatter = new CirceJsonFormatter

      val req = mock[RequestContext]
      val res = mock[ResponseContext]
      val c   = ScalaJsonFormatterA("y2k2mt", "y2.k2mt@xxx.com")

      implicit val encodeBar: Encoder[ScalaJsonFormatterA] =
        Encoder.forProduct2("name", "my-mail")(b => (b.id, b.email))

      val bytes = formatter
        .formatAndWriteInto(req, res, "UTF-8", (c, encodeBar))
        .entity()
        .asInstanceOf[Array[Byte]]
      new String(bytes) === """{"name":"y2k2mt","my-mail":"y2.k2mt@xxx.com"}"""
    }
    "be render with Json object" in {
      import io.circe._
      val formatter = new CirceJsonFormatter

      val req = mock[RequestContext]
      val res = mock[ResponseContext]
      val c   = ScalaJsonFormatterA("y2k2mt", "y2.k2mt@xxx.com")
      val js = Json.obj(
        ("foo", Json.fromString(c.id)),
        ("bar", Json.fromString(c.email))
      )
      val bytes =
        formatter.formatAndWriteInto(req, res, "UTF-8", js).entity().asInstanceOf[Array[Byte]]
      new String(bytes) === """{"foo":"y2k2mt","bar":"y2.k2mt@xxx.com"}"""
    }
  }

}
