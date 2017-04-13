package org.analogweb.scala

import java.util.{ Collection, Collections }
import scala.util.{ Try, Success, Failure }
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import org.analogweb._, core._, util._

class ScalaInvocationMetadataFactory(val routeDef: Option[RouteDef]) extends InvocationMetadataFactory {

  def this() {
    this(None)
  }

  override def createInvocationMetadatas(properties: ApplicationProperties, instances: ContainerAdaptor): Collection[InvocationMetadata] = {
    routeDef.map { route =>
      route.routes.map { d =>
        val metadata: InvocationMetadata = new DefaultScalaInvocationMetadata(d.getClass(), s"${d.method}(${d.rawPath})", Array.empty, d)
        metadata
      }.asJava
    }.getOrElse(
      createInvocationMetadatasReflectively(properties, instances)
    )
  }

  private[this] def createInvocationMetadatasReflectively(properties: ApplicationProperties, instances: ContainerAdaptor): Collection[InvocationMetadata] = {
    collectClasses(properties).flatMap {
      case c if (containsInvocationClass(c)) => {
        obtainInstance(c, instances).map { instance =>
          instance.routes.map { route =>
            new DefaultScalaInvocationMetadata(c, s"${route.method}(${route.rawPath})", Array.empty, route)
          }
        }.getOrElse(Seq.empty[InvocationMetadata])
      }
      case _ => Seq.empty
    }.toSeq.asJava
  }

  protected lazy val classCollectors = Seq(new JarClassCollector(), new FileClassCollector())

  protected def classLoader = Thread.currentThread.getContextClassLoader()

  private[this] def collectClasses(properties: ApplicationProperties) = {
    for {
      packageName <- properties.getComponentPackageNames().asScala ++ Seq(Application.DEFAULT_PACKAGE_NAME)
      uri <- ResourceUtils.findPackageResources(packageName, classLoader).asScala
      collected <- classCollectors.flatMap(_.collect(packageName, uri, classLoader).asScala)
    } yield collected
  }

  private[this] def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[RouteDef].isAssignableFrom(clazz) &&
      classOf[RouteDef].getCanonicalName != clazz.getCanonicalName &&
      classOf[StrictRouteDef].getCanonicalName != clazz.getCanonicalName &&
      classOf[LooseRouteDef].getCanonicalName != clazz.getCanonicalName
  }

  private def obtainInstance(c: Class[_], instances: ContainerAdaptor): Option[RouteDef] = {
    Option(instances.getInstanceOfType(c)).map(_.asInstanceOf[RouteDef]).orElse(instantiate(c).toOption)
  }

  private def instantiate(c: Class[_]): Try[RouteDef] = {
    Try {
      c.getField("MODULE$").get(c).asInstanceOf[RouteDef]
    } recover {
      case e: NoSuchFieldException => c.newInstance.asInstanceOf[RouteDef]
    }
  }
}
