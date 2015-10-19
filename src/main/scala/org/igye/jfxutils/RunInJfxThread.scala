package org.igye.jfxutils

import org.apache.logging.log4j.Logger


object RunInJfxThread {
     def apply(proc: => Unit)(implicit log: Logger): Unit = {
         JfxFuture(proc)
     }
 }
