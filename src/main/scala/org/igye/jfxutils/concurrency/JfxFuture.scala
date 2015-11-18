package org.igye.jfxutils.concurrency

import javafx.application.Platform

import org.apache.logging.log4j.Logger

import scala.concurrent.{Future, Promise}

object JfxFuture {
    def apply[T](proc: => T)(implicit log: Logger): Future[T] = {
        apply(proc, false)
    }

    def apply[T](proc: => T, forcibly: Boolean)(implicit log: Logger): Future[T] = {
        val prom = Promise[T]
        if (forcibly || !Platform.isFxApplicationThread) {
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
