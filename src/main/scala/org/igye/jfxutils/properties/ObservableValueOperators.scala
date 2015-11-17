package org.igye.jfxutils.properties

import javafx.beans.value.{ChangeListener, ObservableValue}

class ObservableValueOperators[T](observableValue: ObservableValue[T]) {
    def ==> (changeListener: ChangeListener[T]): Unit = {
        observableValue.addListener(changeListener)
    }
}
