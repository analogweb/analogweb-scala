package org.analogweb.scala

import java.util.concurrent.{ Executors, ForkJoinPool }
import scala.concurrent.ExecutionContext

object Execution {
  object Implicits {
    implicit val defaultContext: ExecutionContext = createExecutionContext

    def createExecutionContext = {

      def getInt(name: String, f: String => Int): Int =
        try f(System.getProperty(name)) catch { case e: Exception => Runtime.getRuntime.availableProcessors }
      def range(floor: Int, desired: Int, ceiling: Int): Int =
        if (ceiling < floor) range(ceiling, desired, floor) else scala.math.min(scala.math.max(desired, floor), ceiling)

      val desiredParallelism = range(
        getInt("analogweb.threads.min", _.toInt),
        getInt("analogweb.threads", {
          case null | ""               => Runtime.getRuntime.availableProcessors
          case s if s.charAt(0) == 'x' => (Runtime.getRuntime.availableProcessors * s.substring(1).toDouble).ceil.toInt
          case other                   => other.toInt
        }),
        getInt("analogweb.threads.max", _.toInt))
      ExecutionContext.fromExecutorService(new ForkJoinPool(desiredParallelism))
    }
  }
}
