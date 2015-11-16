package org.igye.jfxutils.properties

import javafx.beans.value.{ObservableValue, WritableValue}

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.WritableValueToBindingTarget

class JfxThreadAwareBidirectionalBindingTarget[T](leftBindingTarget: WritableValue[T] with ObservableValue[T])(implicit log: Logger) {
    def <==> (rightBindingTarget: WritableValue[T] with ObservableValue[T]): Unit = {
        leftBindingTarget <== rightBindingTarget
        rightBindingTarget <== leftBindingTarget
    }
}
