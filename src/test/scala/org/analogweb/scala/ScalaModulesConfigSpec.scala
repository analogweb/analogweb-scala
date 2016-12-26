package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core._

@RunWith(classOf[JUnitRunner])
class ScalaModulesConfigSpec extends Specification with Mockito {

  val config = new ScalaModuleConfig

  "ScalaModulesConfig" should {
    "sertainly configure" in {
      val mb = mock[ModulesBuilder]
      mb.addInvocationMetadataFactoriesClass(classOf[ScalaInvocationMetadataFactory]) returns mb
      mb.setInvocationFactoryClass(classOf[ScalaInvocationFactory]) returns mb
      mb.setRenderableResolverClass(classOf[ScalaRenderableResolver]) returns mb
      mb.setResponseHandlerClass(classOf[ScalaResponseHandler]) returns mb
      mb.ignore(classOf[BindAttributeArgumentPreparator]) returns mb
      mb.ignore(classOf[ConsumesMediaTypeVerifier]) returns mb
      mb.ignore(classOf[ScopedMapArgumentPreparator]) returns mb
      val actual = config.prepare(mb)
      actual === mb
    }
  }

}

