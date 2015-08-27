package org.analogweb.scala

import java.util.concurrent.{ Executors, ForkJoinPool }
import scala.concurrent.ExecutionContext

object Execution {
  object Implicits {
    implicit def defaultContext: ExecutionContext = ExecutionContext.fromExecutorService(new ForkJoinPool())
  }
}
