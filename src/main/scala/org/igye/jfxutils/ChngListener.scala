package org.igye.jfxutils

import javafx.beans.value.{ObservableValue, ChangeListener}

case class OldNewValues[T](oldValue: T, newValue:T)

object CngListener {
    def apply[T](body: OldNewValues[T] => Unit): ChangeListener[T] = {
        new ChangeListener[T] {
            override def changed(observable: ObservableValue[_ <: T], oldValue: T, newValue: T): Unit = {
                body(OldNewValues(oldValue, newValue))
            }
        }
    }
}
