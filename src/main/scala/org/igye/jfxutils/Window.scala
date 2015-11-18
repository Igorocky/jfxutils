package org.igye.jfxutils

import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

trait Window {
    var stage: Stage = _

    protected def initWindow(rootNode: Parent): Unit = {
        if (stage == null) {
            stage = new Stage()
        }
        stage.setScene(new Scene(rootNode))
    }

    def open(): Unit = {
        stage.show()
    }

    def close(): Unit = {
        stage.close()
    }
}
