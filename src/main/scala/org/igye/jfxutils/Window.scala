package org.igye.jfxutils

import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage}

trait Window {
    protected var stage: Stage = _

    protected def initWindow(rootNode: Parent, modality: Modality): Unit = {
        stage = new Stage()
        stage.setScene(new Scene(rootNode))
        stage.initModality(modality)
    }

    protected def initWindow(rootNode: Parent): Unit = {
        initWindow(rootNode, Modality.NONE)
    }

    def open(): Unit = {
        stage.show()
    }

    def close(): Unit = {
        stage.close()
    }
}
