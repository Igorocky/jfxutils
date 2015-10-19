package org.igye.jfxutils

import org.apache.logging.log4j.Logger

import scala.concurrent.Await
import scala.concurrent.duration._

object RunInJfxThreadAndReturnResult {
    def apply[T](proc: => T)(implicit log: Logger): T = {
        apply(15 seconds)(proc)
    }

    def apply[T](timeout: Duration)(proc: => T)(implicit log: Logger): T = {
        Await.result(JfxFuture(proc), timeout)
    }
}
