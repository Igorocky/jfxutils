package org.igye.jfxutils.fxml

import javafx.fxml.FXMLLoader

import org.igye.jfxutils.annotations.FxmlFile

import scala.reflect.ClassTag

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

    def load[T <: Initable: ClassTag]: T = {
        load(
            implicitly[ClassTag[T]].runtimeClass
                .getAnnotations.find(_.isInstanceOf[FxmlFile])
                .get.asInstanceOf[FxmlFile].value()
        )
    }
}

