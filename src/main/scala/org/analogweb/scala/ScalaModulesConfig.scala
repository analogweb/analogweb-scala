package org.analogweb.scala

import org.analogweb.{ PluginModulesConfig, InvocationMetadataFactory, ModulesBuilder, ModulesConfig }
import org.analogweb.core.{ BindAttributeArgumentPreparator, ConsumesMediaTypeVerifier, ScopedMapArgumentPreparator }
import org.analogweb.core.response.Json
import org.analogweb.util.PropertyResourceBundleMessageResource
import org.analogweb.util.logging.Logs

class ScalaModuleConfig extends PluginModulesConfig {

  val messageLog = new PropertyResourceBundleMessageResource("org.analogweb.scala.analog-messages")
  val log = Logs.getLog(classOf[ScalaModuleConfig])

  def prepare(builder: ModulesBuilder): ModulesBuilder = {
    log.log(messageLog, "ISB000001")
    builder.addInvocationMetadataFactoriesClass(classOf[ScalaInvocationMetadataFactory])
      .setInvocationFactoryClass(classOf[ScalaInvocationFactory])
      //      .addResponseFormatterClass(classOf[ScalaJsonObject], classOf[ScalaJsonFormatter])
      //      .addResponseFormatterClass(classOf[ScalaJsonText], classOf[ScalaJsonFormatter])
      .setRenderableResolverClass(classOf[ScalaRenderableResolver])
      .setResponseHandlerClass(classOf[ScalaResponseHandler])
      // ignore ApplicationProcessors for Java.
      .ignore(classOf[BindAttributeArgumentPreparator])
      .ignore(classOf[ConsumesMediaTypeVerifier])
      .ignore(classOf[ScopedMapArgumentPreparator])
  }

}
