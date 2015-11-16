package org.igye.jfxutils.properties

import javafx.beans.property.Property
import javafx.beans.value.ObservableValue

class PropertyBindingOperators[T](property: Property[T]) {
    def <== (observableValue: ObservableValue[T]): Unit = {
        property.bind(observableValue)
    }

    def <==> (anotherProperty: Property[T]): Unit = {
        property.bindBidirectional(anotherProperty)
    }
}
