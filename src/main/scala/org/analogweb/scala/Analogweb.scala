package org.analogweb.scala

import org.analogweb._
import org.analogweb.core._

trait Analogweb {
  implicit def path2route(path:String):RequestPathDefinition = RequestPathDefinition.define("/",path)
  def post(path:RequestPathDefinition)(action: Request => Any) = Route("POST",path)(action)
  def get(path:RequestPathDefinition)(action: Request => Any) = Route("GET",path)(action)
}
