package org.analogweb.scala

import java.net.URI
import scala.collection.JavaConverters._
import org.analogweb.{ ApplicationProperties, ApplicationContext, Server, ModulesConfig }
import org.analogweb.core.Servers
import org.analogweb.core.DefaultApplicationProperties._
import org.analogweb.core.DefaultApplicationContext._
import org.analogweb.util.Maps

trait ServerApplications {

  def http(
    host:       String,
    port:       Int,
    properties: Option[ApplicationProperties] = None,
    context:    Option[ApplicationContext]    = None
  )(routes: => Routes): Server =
    server(new URI("http", "", host, port, "", "", ""), properties, context)(routes)

  def https(
    host:       String,
    port:       Int,
    properties: Option[ApplicationProperties] = None,
    context:    Option[ApplicationContext]    = None
  )(routes: => Routes): Server =
    server(new URI("https", "", host, port, "", "", ""), properties, context)(routes)

  def server(
    serverUri:  URI,
    properties: Option[ApplicationProperties] = None,
    ctx:        Option[ApplicationContext]    = None
  )(routes: => Routes): Server = {
    val metadataFactory = new ScalaInvocationMetadataFactory(Some(routes))
    val modulesConfig: ModulesConfig = new ScalaUserModulesConfig(Some(metadataFactory))
    val modulesConfigs: java.util.List[ModulesConfig] = List(modulesConfig).asJava
    Servers.create(
      serverUri,
      properties.getOrElse(defaultProperties()),
      ctx.getOrElse(context(Maps.newEmptyHashMap())),
      modulesConfigs
    )
  }

}
