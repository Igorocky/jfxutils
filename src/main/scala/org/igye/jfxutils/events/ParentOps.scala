package org.igye.jfxutils.events

import javafx.beans.value.ObservableValue
import javafx.scene.Parent

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.RunInJfxThreadForcibly
import org.igye.jfxutils.observableValueToObservableValueOperators
import org.igye.jfxutils.properties.ChgListener

class ParentOps(parent: Parent) {
    def requestLayoutOnChangeOf(values: ObservableValue[_]*)(implicit log: Logger) {
        values.foreach(_ ==> ChgListener{chg=>
            RunInJfxThreadForcibly {
                parent.requestLayout()
            }
        })
    }
}
