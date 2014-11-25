package org.analogweb.scala

import org.analogweb.{ ContainerAdaptor, InvocationMetadata, RequestContext, TypeMapperContext, RequestValueResolvers, Invocation, ResponseContext }
import org.analogweb.core.DefaultInvocationFactory

class ScalaInvocationFactory extends DefaultInvocationFactory {

  override def createInvocation(ca: ContainerAdaptor, im: InvocationMetadata, rc: RequestContext, rsc: ResponseContext, tc: TypeMapperContext, rvr: RequestValueResolvers): Invocation = {
    im match {
      case sim: ScalaInvocationMetadata => new ScalaInvocation(sim.getDefinedPath, sim.route, rc, rsc, tc, rvr, im)
      case _                            => super.createInvocation(ca, im, rc, rsc, tc, rvr)
    }
  }
}

