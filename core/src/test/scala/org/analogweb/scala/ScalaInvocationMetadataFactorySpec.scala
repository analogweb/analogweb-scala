package org.analogweb.scala

import java.util.ArrayList
import scala.collection.JavaConverters._
import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb.{ContainerAdaptor, ApplicationProperties}

@RunWith(classOf[JUnitRunner])
class ScalaInvocationMetadataFactorySpec extends Specification with Mockito {

  val factory =
    new ScalaInvocationMetadataFactory

  "ScalaInvocationMetadataFactory" should {
    "Create InvocationMetadata NOT work" in {
      val ca =
        mock[ContainerAdaptor]
      val ap =
        mock[ApplicationProperties]
      ap.getComponentPackageNames() returns new ArrayList()
      val collected = factory
        .createInvocationMetadatas(ap, ca)
        .asScala
        .toSet
      val actual = collected
        .find(x => x.asInstanceOf[ScalaInvocationMetadata].route.rawPath == "/baabaz")
        .headOption
      actual === None
    }
    "Create InvocationMetadata from inner RouteDef" in {
      import analogweb._
      val f = new ScalaInvocationMetadataFactory(
        Some(
          get("/foo/bar") { _ =>
            "Hello"
          }
        ))
      val ca =
        mock[ContainerAdaptor]
      val ap =
        mock[ApplicationProperties]
      ap.getComponentPackageNames() returns new ArrayList()
      val collected = f
        .createInvocationMetadatas(ap, ca)
        .asScala
        .toSet
      collected.size === 1
    }
  }

}

class Foo {
  import analogweb._
  get("/imf/foo") { r =>
    "Foo"
  }
}

class Baa {
  val baabaz = { (r: Request) =>
    "Foo"
  }
}
