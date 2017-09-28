package org.analogweb.scala

import org.analogweb.{
  PluginModulesConfig,
  InvocationMetadataFactory,
  ModulesBuilder,
  UserModulesConfig
}
import org.analogweb.core.{
  BindAttributeArgumentPreparator,
  ConsumesMediaTypeVerifier,
  ScopedMapArgumentPreparator
}
import org.analogweb.util.PropertyResourceBundleMessageResource
import org.analogweb.util.logging.Logs

class ScalaUserModulesConfig(
    invocationMetadataFactory: Option[InvocationMetadataFactory] = None,
    invocationFactory: Option[ScalaInvocationFactory] = None,
    renderableResolver: Option[ScalaRenderableResolver] = None,
    responseHandler: Option[ScalaResponseHandler] = None
) extends ScalaModulesConfig(invocationMetadataFactory,
                               invocationFactory,
                               renderableResolver,
                               responseHandler)
    with UserModulesConfig

class ScalaModulesConfig(
    invocationMetadataFactory: Option[InvocationMetadataFactory] = None,
    invocationFactory: Option[ScalaInvocationFactory] = None,
    renderableResolver: Option[ScalaRenderableResolver] = None,
    responseHandler: Option[ScalaResponseHandler] = None
) extends PluginModulesConfig {

  def this() {
    this(None, None, None, None)
  }

  val messageLog =
    new PropertyResourceBundleMessageResource("org.analogweb.scala.analog-messages")
  val log =
    Logs.getLog(classOf[ScalaModulesConfig])

  def prepare(builder: ModulesBuilder): ModulesBuilder = {
    log
      .log(messageLog, "ISB000001")
    val im = invocationMetadataFactory
      .map(builder.addInvocationMetadataFactories(_))
      .getOrElse(
        builder.addInvocationMetadataFactoriesClass(classOf[ScalaInvocationMetadataFactory])
      )
    val in = invocationFactory
      .map(im.setInvocationFactory(_))
      .getOrElse(
        im.setInvocationFactoryClass(classOf[ScalaInvocationFactory])
      )
    val rr = renderableResolver
      .map(in.setRenderableResolver(_))
      .getOrElse(
        in.setRenderableResolverClass(classOf[ScalaRenderableResolver])
      )
    val rh = responseHandler
      .map(rr.setResponseHandler(_))
      .getOrElse(
        rr.setResponseHandlerClass(classOf[ScalaResponseHandler])
      )
    // ignore ApplicationProcessors for Java.
    rh.ignore(classOf[BindAttributeArgumentPreparator])
      .ignore(classOf[ConsumesMediaTypeVerifier])
      .ignore(classOf[ScopedMapArgumentPreparator])
  }

}
