package org.analogweb.json4s

import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.analogweb._, scala._

class ScalaModulesConfigSpec extends Specification with Mockito {

  val config = new Json4sModuleConfig

  "ScalaModulesConfig" should {
    "sertainly configure" in {
      val mb = mock[ModulesBuilder]
      mb.addResponseFormatterClass(classOf[ScalaJsonObject], classOf[Json4sJsonFormatter]) returns mb
      mb.addResponseFormatterClass(classOf[ScalaJsonText], classOf[Json4sJsonFormatter]) returns mb
      val actual = config.prepare(mb)
      actual === mb
    }
  }

}
