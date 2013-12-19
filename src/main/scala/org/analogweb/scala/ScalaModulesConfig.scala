package org.analogweb.scala

import org.analogweb.{PluginModulesConfig, InvocationMetadataFactory, ModulesBuilder, ModulesConfig}
import org.analogweb.util.logging.Logs

class ScalaModuleConfig extends PluginModulesConfig {
  val log = Logs.getLog(classOf[ScalaModuleConfig])
  def prepare(p1: ModulesBuilder): ModulesBuilder = {
    log.info("Scala Plugin!")
    p1.addInvocationMetadataFactoriesClass(classOf[ScalaInvocationMetadataFactory])
    p1.setInvocationFactoryClass(classOf[ScalaInvocationFactory])
  }
}
