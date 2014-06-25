package org.analogweb.scala

import org.analogweb.{ PluginModulesConfig, InvocationMetadataFactory, ModulesBuilder, ModulesConfig }
import org.analogweb.util.logging.Logs

class ScalaModuleConfig extends PluginModulesConfig {

  val log = Logs.getLog(classOf[ScalaModuleConfig])

  def prepare(builder: ModulesBuilder): ModulesBuilder = {
    log.info("Scala Plugin!")
    builder.addInvocationMetadataFactoriesClass(classOf[ScalaInvocationMetadataFactory])
      .setInvocationFactoryClass(classOf[ScalaInvocationFactory])
      .addResponseFormatterClass(classOf[ScalaJson], classOf[ScalaJsonFormatter])
  }

}
