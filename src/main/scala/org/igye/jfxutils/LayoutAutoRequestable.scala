package org.igye.jfxutils

import javafx.beans.value.ObservableValue

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.concurrency.RunInJfxThreadForcibly
import org.igye.jfxutils.properties.ChgListener

trait LayoutAutoRequestable {
    def requestLayout(): Unit

    protected def requestLayoutOnChangeOf(values: ObservableValue[_]*)(implicit log: Logger) {
        values.foreach(_ ==> ChgListener{chg=>
            RunInJfxThreadForcibly {
                requestLayout()
            }
        })
    }
}
