package org.igye.jfxutils

import javafx.fxml.FXMLLoader

object FxmlSupport {
    def load[T <: Initable](fxmlPath: String): T = {
        val fxmlUrl = this.getClass().getClassLoader().getResource(fxmlPath)
        val loader = new FXMLLoader()
        loader.setLocation(fxmlUrl)
        loader.load[T]()
        val ctrl = loader.getController[T]
        ctrl.init()
        ctrl
    }
}

