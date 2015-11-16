package org.igye.jfxutils

import javafx.collections.ListChangeListener
import javafx.collections.ListChangeListener.Change

object ListChgListener {
    def apply[T](body: Change[_ <: T] => Unit): ListChangeListener[T] = {
        new ListChangeListener[T] {
            override def onChanged(c: Change[_ <: T]): Unit = {
                body(c)
            }
        }
    }
}
