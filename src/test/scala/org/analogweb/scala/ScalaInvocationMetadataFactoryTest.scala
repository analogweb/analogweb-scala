package org.analogweb.scala

import org.scalatest.Assertions
import org.scalatest.matchers.MustMatchers
import org.junit.Test

class ScalaInvocationMetadataFactoryTest extends Assertions with MustMatchers {
  val factory = new ScalaInvocationMetadataFactory
  @Test def testContainsCreateInvocationClass(){
    val actual = factory.containsInvocationClass(classOf[Foo])
    actual must be(true)
  }
}

class Foo extends Analogweb {
  get("/foo") {
    "Foo"
  }
}

