package org.igye.jfxutils.fxml

import javafx.fxml.FXMLLoader
import javafx.stage.Stage

import org.igye.jfxutils.Window
import org.igye.jfxutils.annotations.FxmlFile

import scala.reflect.ClassTag

object FxmlSupport {
    def load[T <: Initable](fxmlPath: String, primaryStage: Stage): T = {
        val fxmlUrl = this.getClass().getClassLoader().getResource(fxmlPath)
        val loader = new FXMLLoader()
        loader.setLocation(fxmlUrl)
        loader.load[T]()
        val ctrl = loader.getController[T]
        if (primaryStage != null) {
            ctrl.asInstanceOf[Window].stage = primaryStage
        }
        ctrl.init()
        ctrl
    }

    def load[T <: Initable: ClassTag](primaryStage: Stage): T = {
        load(
            implicitly[ClassTag[T]].runtimeClass
                .getAnnotations.find(_.isInstanceOf[FxmlFile])
                .get.asInstanceOf[FxmlFile].value(),
            primaryStage
        )
    }

    def load[T <: Initable: ClassTag]: T = {
        load(
            implicitly[ClassTag[T]].runtimeClass
                .getAnnotations.find(_.isInstanceOf[FxmlFile])
                .get.asInstanceOf[FxmlFile].value(),
            null
        )
    }
}

