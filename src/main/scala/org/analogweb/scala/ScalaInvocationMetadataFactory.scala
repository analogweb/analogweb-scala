package org.analogweb.scala

import java.util.{ Collection, Collections }
import scala.util.{ Try, Success, Failure }
import scala.collection.mutable.ArrayBuffer
import scala.collection.convert.decorateAsJava._
import org.analogweb.{ ContainerAdaptor, InvocationMetadata, InvocationMetadataFactory }
import org.analogweb.core.DefaultInvocationMetadata

class ScalaInvocationMetadataFactory extends InvocationMetadataFactory {

  def containsInvocationClass(clazz: Class[_]): Boolean = {
    classOf[RouteDef].isAssignableFrom(clazz) && classOf[RouteDef].getCanonicalName != clazz.getCanonicalName
  }

  def createInvocationMetadatas(clazz: Class[_], instances: ContainerAdaptor): Collection[InvocationMetadata] = {
    clazz match {
      case c if (classOf[RouteDef].isAssignableFrom(c) && classOf[Analogweb].getCanonicalName() != c.getCanonicalName()) => {
        val routes = obtainInstance(c, instances).routes
        val metadatas: Seq[InvocationMetadata] = routes.map { route =>
          new DefaultScalaInvocationMetadata(clazz, s"${route.method}(${route.rawPath})", Array.empty, route)
        }
        metadatas.asJavaCollection
      }
      case _ => Seq.empty.asJava
    }
  }

  private def obtainInstance(c: Class[_], instances: ContainerAdaptor): RouteDef = {
    Try {
      Option(instances.getInstanceOfType(c)).map(_.asInstanceOf[RouteDef]).getOrElse(instantiate(c))
    }.getOrElse(instantiate(c))
  }

  private def instantiate(c: Class[_]): RouteDef = {
    val r = Try {
      c.getField("MODULE$").get(c).asInstanceOf[RouteDef]
    } recover {
      case e: NoSuchFieldException => c.newInstance.asInstanceOf[RouteDef]
    }
    r match {
      case Success(s) => s
      case Failure(e) => throw e
    }
  }
}
