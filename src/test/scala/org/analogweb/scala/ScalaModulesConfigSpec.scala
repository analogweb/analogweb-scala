package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._

@RunWith(classOf[JUnitRunner])
class ScalaModulesConfigSpec extends Specification with Mockito {

  val config = new ScalaModuleConfig

  "ScalaModulesConfig" should {
    "sertainly configure" in {
      val mb = mock[ModulesBuilder]
      mb.addInvocationMetadataFactoriesClass(classOf[ScalaInvocationMetadataFactory]) returns mb
      mb.setInvocationFactoryClass(classOf[ScalaInvocationFactory]) returns mb
      mb.addResponseFormatterClass(classOf[ScalaJsonObject], classOf[ScalaJsonFormatter]) returns mb
      mb.addResponseFormatterClass(classOf[ScalaJsonText], classOf[ScalaJsonFormatter]) returns mb
      mb.setRenderableResolverClass(classOf[ScalaRenderableResolver]) returns mb
      mb.setResponseHandlerClass(classOf[ScalaResponseHandler]) returns mb
      val actual = config.prepare(mb)
      actual === mb
    }
  }

}

