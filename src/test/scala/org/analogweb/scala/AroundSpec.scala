package org.analogweb.scala

import org.specs2.mutable._
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.runner.JUnitRunner
import org.analogweb.core.response.HttpStatus
import org.analogweb.core.response.Text
import org.analogweb.scala.Responses._

@RunWith(classOf[JUnitRunner])
class AroundSpec extends Specification with Mockito {

  val analogweb = new StubApplication

  "Around" should {
    "pass before rejection and append after" in {
      val route = analogweb.routes(0)
      val request = mock[Request]
      val actual = route.invoke(request)
      actual must_== Ok
      actual match {
        case s: HttpStatus => s.getRenderable match {
          case t: Text => t.toString must_== "FooBar"
        }
      }
    }
    "rejected" in {
      val route = analogweb.routes(1)
      val request = mock[Request]
      val actual = route.invoke(request)
      actual must_== BadRequest
    }
    "pass before rejection and throwgh after" in {
      val route = analogweb.routes(2)
      val request = mock[Request]
      val actual = route.invoke(request)
      actual.toString must_== "Foo"
    }
    "combined arounds" in {
      val route = analogweb.routes(3)
      val request = mock[Request]
      val actual = route.invoke(request)
      actual must_== BadRequest
    }
  }
}

class StubApplication extends Analogweb {
  implicit val around1 = before { implicit r =>
    pass()
  } :+ after {
    case s: String => Ok(asText(s + "Bar"))
  }
  get("/foo") { r =>
    "Foo"
  }

  val around2 = before { implicit r =>
    reject(BadRequest)
  } :+ after {
    case s: String => Ok(asText(s + "Bar"))
  }
  post("/foo") { r =>
    "Foo"
  }(around2)

  val around3 = before { implicit r =>
    pass()
  } :+ after {
    case i: Int => Ok(asText((i + 10).toString))
  }
  put("/foo") { r =>
    "Foo"
  }(around3)

  val around4 = around2 ++ around3
  delete("/foo") { r =>
    "Foo"
  }(around4)
}

