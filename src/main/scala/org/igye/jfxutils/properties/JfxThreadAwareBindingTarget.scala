package org.igye.jfxutils.properties

import javafx.beans.value.{ObservableValue, WritableValue}

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.{ChgListener, RunInJfxThread}

class JfxThreadAwareBindingTarget[T](bindingTarget: WritableValue[T])(implicit log: Logger) {
    def <== (observableValue: ObservableValue[T]): Unit = {
        RunInJfxThread {
            bindingTarget.setValue(observableValue.getValue)
        }
        observableValue.addListener(ChgListener[T]{ chg =>
            RunInJfxThread {
                bindingTarget.setValue(chg.newValue)
            }
        })
    }
}
