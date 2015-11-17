package org.igye.jfxutils.properties

import javafx.beans.Observable
import javafx.beans.binding.ObjectBinding

object Expr {
    def apply[T](dependencies: Observable*)(calcValue: => T): ObjectBinding[T] = {
        new ObjectBinding[T] {
            bind(dependencies: _*)
            override def computeValue(): T = {
                calcValue
            }
        }
    }
}
