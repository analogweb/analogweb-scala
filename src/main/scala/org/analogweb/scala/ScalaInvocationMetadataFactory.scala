package org.analogweb.scala

import java.util.{ Collection, Collections }
import scala.util.{ Try, Success, Failure }
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import org.analogweb._, core._, util._

class ScalaInvocationMetadataFactory(val routeDef: Option[Routes]) extends InvocationMetadataFactory {

  def this() {
    this(None)
  }

  override def createInvocationMetadatas(properties: ApplicationProperties, instances: ContainerAdaptor): Collection[InvocationMetadata] = {
    routeDef.map { route =>
      route.routes.mapRoute { d =>
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
          instance.routes.mapRoute { route =>
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
    classOf[Routes].isAssignableFrom(clazz) &&
      classOf[Routes].getCanonicalName != clazz.getCanonicalName &&
      classOf[RouteDef].getCanonicalName != clazz.getCanonicalName &&
      classOf[StrictRouteDef].getCanonicalName != clazz.getCanonicalName &&
      classOf[LooseRouteDef].getCanonicalName != clazz.getCanonicalName &&
      classOf[Analogweb].getCanonicalName != clazz.getCanonicalName
  }

  private def obtainInstance(c: Class[_], instances: ContainerAdaptor): Option[Routes] = {
    Option(instances.getInstanceOfType(c)).map(_.asInstanceOf[Routes]).orElse(instantiate(c).toOption)
  }

  private def instantiate(c: Class[_]): Try[Routes] = {
    Try {
      c.getField("MODULE$").get(c).asInstanceOf[Routes]
    } recover {
      case e: NoSuchFieldException => c.newInstance.asInstanceOf[Routes]
    }
  }
}
