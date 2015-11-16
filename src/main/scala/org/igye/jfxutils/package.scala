package org.igye

import javafx.beans.value.{ObservableValue, WritableValue}
import javafx.scene.Node

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.properties.{JfxThreadAwareBidirectionalBindingTarget, JfxThreadAwareBindingTarget}

package object jfxutils {
    implicit def nodeToHasEvens(node: Node) = new HasEvents(node)

    implicit def WritableValueToBindingTarget[T](writableValue: WritableValue[T])(implicit log: Logger) = {
        new JfxThreadAwareBindingTarget[T](writableValue)
    }

    implicit def WritableObservableValueToBidirectionalBindingTarget[T]
            (writableObservableValue: WritableValue[T] with ObservableValue[T])(implicit log: Logger) = {
        new JfxThreadAwareBidirectionalBindingTarget[T](writableObservableValue)
    }
}
