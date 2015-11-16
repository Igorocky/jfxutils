package org.igye.jfxutils.properties

import javafx.beans.property.SimpleStringProperty

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.RunInJfxThreadAndReturnResult

class StringProperty(implicit log: Logger) {
    val prop: SimpleStringProperty = new SimpleStringProperty()

    def set(str: String): Unit = {
        RunInJfxThreadAndReturnResult {
            prop.set(str)
        }
    }

    def get: String = {
        RunInJfxThreadAndReturnResult {
            prop.get()
        }
    }
}
