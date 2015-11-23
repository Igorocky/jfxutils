package org.igye.jfxutils

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.{Node, Parent}

import org.igye.jfxutils.events.{NodeOps, ParentOps}
import org.igye.jfxutils.properties.{ListOperators, ObservableValueOperators, PropertyOperators}

object Implicits {
    implicit def nodeToNodeOps(node: Node) = new NodeOps(node)

    implicit def parentToParentOps(parent: Parent) = new ParentOps(parent)

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
