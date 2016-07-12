package org.analogweb.scala

import org.analogweb.Renderable

trait Adoption
case class reject(reason: Renderable) extends Adoption
case class pass() extends Adoption
case class passWith[T](val key: String = "", val result: T) extends Adoption

trait Around
case class before(action: Request => Adoption) extends Around
case class after(action: PartialFunction[Any, Renderable]) extends Around

case class Arounds(arounds: Seq[Around] = Seq()) {
  def :+(around: Around) = Arounds(arounds :+ around)
  def ++(other: Arounds) = Arounds(arounds ++ other.arounds)
  def allAfter = arounds.collect { case a: after => a }
  def allBefore = arounds.collect { case b: before => b }
}
