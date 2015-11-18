package org.igye

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.Node

import org.igye.jfxutils.events.HasEvents
import org.igye.jfxutils.properties.{ObservableValueOperators, ListOperators, PropertyOperators}

package object jfxutils {
    implicit def nodeToHasEvens(node: Node) = new HasEvents(node)

    implicit def propertyToPropertyOperators[T](property: Property[T]): PropertyOperators[T] = {
        new PropertyOperators(property)
    }

    implicit def listToListOperators[T](list: java.util.List[T]): ListOperators[T] = {
        new ListOperators(list)
    }

    implicit def observableValueToObservableValueOperators[T](observableValue: ObservableValue[T]): ObservableValueOperators[T] = {
        new ObservableValueOperators(observableValue)
    }
}
