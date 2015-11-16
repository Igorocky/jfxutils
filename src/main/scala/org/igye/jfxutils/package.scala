package org.igye

import javafx.beans.property.Property
import javafx.scene.Node

import org.igye.jfxutils.properties.{ListBindingOperators, PropertyBindingOperators}

package object jfxutils {
    implicit def nodeToHasEvens(node: Node) = new HasEvents(node)

    implicit def propertyToBindingOperators[T](property: Property[T]): PropertyBindingOperators[T] = {
        new PropertyBindingOperators[T](property)
    }

    implicit def listToListBindingOperators[T](list: java.util.List[T]): ListBindingOperators[T] = {
        new ListBindingOperators[T](list)
    }
}
