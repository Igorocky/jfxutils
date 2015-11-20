package org.igye.jfxutils.properties

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

import org.igye.jfxutils.propertyToPropertyOperators

class PropertyOperators[T](property: Property[T]) {
    def <== (observableValue: ObservableValue[T]): Unit = {
        property.bind(observableValue)
    }

    def <== (anotherProperty: Property[T]): Property[T] = {
        property <== anotherProperty.asInstanceOf[ObservableValue[T]]
        anotherProperty
    }

    def <==> (anotherProperty: Property[T]): Unit = {
        property.bindBidirectional(anotherProperty)
    }
}
