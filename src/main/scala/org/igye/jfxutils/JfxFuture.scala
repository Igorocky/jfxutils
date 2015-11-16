package org.igye.jfxutils

import javafx.application.Platform

import org.apache.logging.log4j.Logger

import scala.concurrent.{Future, Promise}

object JfxFuture {
    private var jfxThread: Thread = _

    def setJfxThread(thread: Thread): Unit = {
        jfxThread = thread
    }

    def apply[T](proc: => T)(implicit log: Logger): Future[T] = {
        apply(proc, false)
    }

    def apply[T](proc: => T, forcibly: Boolean)(implicit log: Logger): Future[T] = {
        val prom = Promise[T]
        if (jfxThread == null) {
            log.warn("jfxThread == null")
        }
        if (forcibly || (jfxThread != null && jfxThread != Thread.currentThread())) {
            Platform.runLater(new Runnable {
                override def run(): Unit = {
                    completePromise(prom, proc)
                }
            })
        } else {
            completePromise(prom, proc)
        }
        prom.future
    }

    private def completePromise[T](promise: Promise[T], proc: => T)(implicit log: Logger): Unit = {
        try {
            promise.success(proc)
        } catch {
            case t: Throwable =>
                log.error(t.getMessage, t)
                promise.failure(t)
        }
    }
}
