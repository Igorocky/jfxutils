package org.igye.jfxutils.properties

import javafx.beans.value.{ChangeListener, ObservableValue}

case class OldNewValues[T](observable: ObservableValue[_ <: T], oldValue: T, newValue:T)

object ChgListener {
    def apply[T](body: OldNewValues[T] => Unit): ChangeListener[T] = {
        new ChangeListener[T] {
            override def changed(observable: ObservableValue[_ <: T], oldValue: T, newValue: T): Unit = {
                body(OldNewValues(observable, oldValue, newValue))
            }
        }
    }
}
