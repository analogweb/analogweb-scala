package org.analogweb.scala

import org.analogweb.Renderable

trait Rejection
case class reject(reason: Renderable) extends Rejection
case class pass() extends Rejection

trait Around
case class before(action: Request => Rejection) extends Around
case class after(action: PartialFunction[Any, Renderable]) extends Around

case class Arounds(arounds: Seq[Around] = Seq()) {
  def :+(around: Around) = Arounds(arounds :+ around)
  def ++(other: Arounds) = Arounds(arounds ++ other.arounds)
  def allAfter = arounds.collect { case a: after => a }
  def allBefore = arounds.collect { case b: before => b }
}
