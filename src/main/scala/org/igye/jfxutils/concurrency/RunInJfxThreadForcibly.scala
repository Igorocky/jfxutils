package org.igye.jfxutils.concurrency

import org.apache.logging.log4j.Logger

object RunInJfxThreadForcibly {
     def apply(proc: => Unit)(implicit log: Logger): Unit = {
         JfxFuture(proc, true)
     }
 }
