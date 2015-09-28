package org.igye.jfxutils

import org.slf4j.Logger

object RunInJfxThreadForcibly {
     def apply(proc: => Unit)(implicit log: Logger): Unit = {
         JfxFuture(proc, true)
     }
 }
