package org.analogweb.scala

import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.analogweb._
import org.analogweb.core.ParameterValueResolver
import org.analogweb.scala._

@RunWith(classOf[JUnitRunner])
class ResolversSpec extends Specification with Mockito with Analogweb with Resolvers {

  trait mocks extends org.specs2.specification.Scope {
    var rc = mock[RequestContext]
    var rvr = mock[RequestValueResolvers]
    var im = mock[ScalaInvocationMetadata]
    var tc = mock[TypeMapperContext]
    var qp = mock[Parameters]
    var rh = mock[Headers]
    var r = new Request(rc, rvr, im, tc)
  }

  "Valid Get Route" in new mocks {
    rc.getQueryParameters returns qp
    qp.getValues("foo") returns java.util.Collections.emptyList()
    qp.getValues("baa") returns java.util.Arrays.asList("baz")
    rvr.findRequestValueResolver(classOf[ParameterValueResolver]) returns new ParameterValueResolver()
    val actual = get("/foo") { implicit r =>
      s"${parameter.of("baa").getOrElse("a")}"
    }
    actual(0).invoke(r)
  }

}

