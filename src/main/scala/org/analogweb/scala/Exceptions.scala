package org.analogweb.scala

import org.analogweb._

case class ResolverNotFound(val name: String) extends RuntimeException
case class NoValuesResolved[T](val key: String, val resolver: RequestValueResolver, val requiredType: Class[T]) extends RuntimeException
case class ResolvedValueTypeMismatched[T](val resolved: Any, val requiredType: Class[T]) extends RuntimeException
