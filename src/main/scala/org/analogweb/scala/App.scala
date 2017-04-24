class A {

  import analogweb._

  http("localhost", 8000) {
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
