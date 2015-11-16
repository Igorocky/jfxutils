package org.igye

import javafx.beans.property.Property
import javafx.scene.Node

import org.igye.jfxutils.properties.BindingOperators

package object jfxutils {
    implicit def nodeToHasEvens(node: Node) = new HasEvents(node)

    implicit def propertyToBindingOperators[T](property: Property[T]): BindingOperators[T] = {
        new BindingOperators[T](property)
    }
}
