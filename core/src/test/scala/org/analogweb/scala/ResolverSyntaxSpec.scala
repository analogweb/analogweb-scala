package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core._
import java.lang.annotation.Annotation

@RunWith(classOf[JUnitRunner])
class ResolverSyntaxSpec extends Specification with Mockito {

  class MockRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext,
                                    metadata: InvocationMetadata,
                                    key: String,
                                    requiredType: Class[_],
                                    annoattions: Array[Annotation]): AnyRef =
      if (key == "foo")
        "That's it"
      else
        null
  }
  class NumberRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext,
                                    metadata: InvocationMetadata,
                                    key: String,
                                    requiredType: Class[_],
                                    annoattions: Array[Annotation]): AnyRef =
      if (key == "foo")
        Integer
          .valueOf(1)
      else
        null
  }
  class OptionRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext,
                                    metadata: InvocationMetadata,
                                    key: String,
                                    requiredType: Class[_],
                                    annoattions: Array[Annotation]): AnyRef =
      if (key == "foo")
        Option("That's it")
      else
        None
  }

  class ScalaSpecificRequestValueResolver extends RequestValueResolver {
    override final def resolveValue(request: RequestContext,
                                    metadata: InvocationMetadata,
                                    key: String,
                                    requiredType: Class[_],
                                    annoattions: Array[Annotation]): AnyRef =
      if (key == "foo")
        Option("That's it")
      else
        None
  }
  class SpecificRequestValueResolver extends SpecificMediaTypeRequestValueResolver {
    override final def resolveValue(request: RequestContext,
                                    metadata: InvocationMetadata,
                                    key: String,
                                    requiredType: Class[_],
                                    annoattions: Array[Annotation]): AnyRef =
      "That's it"
    override final def supports(mediaType: MediaType) =
      mediaType
        .toString()
        .startsWith("text/plain")
  }

  class ScalaBooRequestValueResolver extends ScalaRequestValueResolver {
    def resolve[A](
        request: RequestContext,
        metadata: InvocationMetadata,
        key: String,
        requiredType: Class[A]
    )(implicit context: ResolverContext): Either[NoValuesResolved[A], A] = {
      if (requiredType == classOf[String])
        Right(
          (key + " boo")
            .asInstanceOf[A])
      else
        Left(NoValuesResolved(key, this, requiredType))
    }
  }

  val mockResolver =
    classOf[MockRequestValueResolver]
  val numberResolver =
    classOf[NumberRequestValueResolver]
  val optionResolver =
    classOf[OptionRequestValueResolver]
  val specificResolver =
    classOf[SpecificRequestValueResolver]
  val scalaResolver =
    new ScalaBooRequestValueResolver()

  trait mocks extends org.specs2.specification.Scope {
    val rc =
      mock[RequestContext]
    val rvr =
      mock[RequestValueResolvers]
    val im =
      mock[ScalaInvocationMetadata]
    val tc =
      mock[TypeMapperContext]
    val qp =
      mock[Parameters]
    val rh =
      mock[Headers]
    val request = new Request(rc, rvr, im, tc)
  }

  "Scope" should {
    "Returns avairable scope" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(mockResolver, request)
      actual
        .as[String]("foo")
        .right
        .toOption
        .get must be("That's it")
    }
    "Returns avairable scope and converters" in new mocks {
      rvr.findRequestValueResolver(numberResolver) returns new NumberRequestValueResolver()
      tc.mapToType(classOf[TypeMapper],
                   Integer
                     .valueOf(1),
                   classOf[String],
                   Array()) returns "One"
      val actual =
        ReflectiveResolverSyntax(numberResolver, request)
      actual
        .asOption[String]("foo") must beSome(===("One"))
    }
    "Returns avairable scope and not avairable converters" in new mocks {
      rvr.findRequestValueResolver(numberResolver) returns new NumberRequestValueResolver()
      tc.mapToType(classOf[TypeMapper],
                   Integer
                     .valueOf(1),
                   classOf[String],
                   Array()) returns null
      val actual =
        ReflectiveResolverSyntax(numberResolver, request)
      actual
        .asOption[String]("foo") must beNone
    }
    "Returns not avairable scope of" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(mockResolver, request)
      actual
        .asOption[String]("bar") must beNone
    }
    "Returns avairable scope via get" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(mockResolver, request)
      actual
        .asOption[String]("foo") must_== Some("That's it")
    }
    "Returns not avairable scope via get" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(mockResolver, request)
      actual
        .asOption[String]("bar") must beNone
    }
    "Returns not avairable scope" in new mocks {
      rvr.findRequestValueResolver(mockResolver) returns new MockRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(optionResolver, request)
      actual
        .as[String]("foo")
        .right
        .toOption must beNone
    }
    "Returns option value via get" in new mocks {
      rvr.findRequestValueResolver(optionResolver) returns new OptionRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(optionResolver, request)
      actual
        .asOption[String]("foo") must_== Some("That's it")
    }
    "Returns none value via get" in new mocks {
      rvr.findRequestValueResolver(optionResolver) returns new OptionRequestValueResolver()
      val actual =
        ReflectiveResolverSyntax(optionResolver, request)
      actual
        .asOption[String]("bar") must_== None
    }
    "Supports content types" in new mocks {
      rvr.findRequestValueResolver(specificResolver) returns new SpecificRequestValueResolver()
      rc.getContentType() returns MediaTypes.TEXT_PLAIN_TYPE
      val actual =
        ReflectiveResolverSyntax(specificResolver, request)
      actual
        .as[String]("foo")
        .right
        .toOption must beSome(===("That's it"))
    }
    "Not supports content types" in new mocks {
      rvr.findRequestValueResolver(specificResolver) returns new SpecificRequestValueResolver()
      rc.getContentType() returns MediaTypes.APPLICATION_JSON_TYPE
      val actual =
        ReflectiveResolverSyntax(specificResolver, request)
      actual
        .as[String]("foo")
        .right
        .toOption must beNone
    }
    "Returns scala specific resolver" in new mocks {
      val actual =
        InstanceResolverSyntax(scalaResolver, request)
      actual
        .as[String]("foo")
        .right
        .toOption
        .get must_== "foo boo"
    }
    "Returns scala specific resolver not value resolved" in new mocks {
      val resolverInstance =
        new ScalaBooRequestValueResolver()
      val actual =
        InstanceResolverSyntax(scalaResolver, request)
      val actualLeft = actual
        .as[Int]
        .left
        .get
        .asInstanceOf[NoValuesResolved[_]]
      actualLeft.key must_== ""
      actualLeft.requiredType must_== classOf[Int]
    }
  }
}
