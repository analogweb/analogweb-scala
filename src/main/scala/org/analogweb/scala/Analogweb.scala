package org.analogweb.scala

import org.analogweb._
import org.analogweb.core._

trait Analogweb {
  var actionsMap = collection.mutable.Map[RequestPathDefinition,Any]()
  var methodsMap = collection.mutable.Map[RequestPathDefinition,Array[String]]()
  implicit def path2route(path:String):RequestPathDefinition = RequestPathDefinition.define("/",path)
  def post(path:RequestPathDefinition)(action: => Any):Unit = route(path,Array("POST"))(action)
  def get(path:RequestPathDefinition)(action: => Any):Unit = route(path,Array("GET"))(action)
  def route(path:RequestPathDefinition,methods:Array[String])(action: => Any) = {
    actionsMap += (path -> action)
    methodsMap += (path -> methods)
  }
}
