package org.analogweb.scala

import java.net.URI
import org.analogweb._, scala._
import org.analogweb.core.Servers

object App {

  def apply(serverUri: String)(routes: => Routes): Server = apply(URI.create(serverUri))(routes)

  def apply(
    serverUri:  URI,
    properties: Option[ApplicationProperties] = None,
    context:    Option[ApplicationContext]    = None
  )(routes: => Routes): Server = ???
}

class A {

  import analogweb._

  App("http://localhost:8000") {
    get("/ping") { r =>
      "PONG"
    } ~
      post("/ping") { r =>
        "PONG"
      } ~
      delete("/ping") { r =>
        "PONG"
      }
  }.run

}
