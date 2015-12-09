package org.igye.jfxutils

import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeLineCap, StrokeLineJoin, StrokeType}
import javafx.scene.{Node, Scene}

import org.apache.logging.log4j.Logger
import org.igye.jfxutils.Implicits.{observableValueToObservableValueOperators, sceneToSceneOps}
import org.igye.jfxutils.action.Action
import org.igye.jfxutils.dialog.{FileChooserType, TextFieldFileChooser, TextFieldVarNameAutocomplete}
import org.igye.jfxutils.events.EventHandlerInfo
import org.igye.jfxutils.properties.ChgListener

import scala.reflect.ClassTag

object JfxUtils {
    def createBorder(color: Color, borderWidths: Double): Border = {
        new Border(
            new BorderStroke(
                color,
                new BorderStrokeStyle(
                    StrokeType.INSIDE,
                    StrokeLineJoin.MITER,
                    StrokeLineCap.BUTT,
                    10,
                    0,
                    null
                ),
                CornerRadii.EMPTY,
                new BorderWidths(borderWidths),
                new Insets(0)
            )
        )
    }

    def createBorder(color: Color): Border = {
        createBorder(color, 1)
    }

    def createBackground(fillColor: Color) = {
        new Background(new BackgroundFill(fillColor, CornerRadii.EMPTY, new Insets(0)))
    }

    def bindActionsToSceneProp(sceneProp: ObservableValue[Scene], actions: List[Action]): Unit = {
        var eventHandlers: Option[List[EventHandlerInfo[KeyEvent]]] = None
        sceneProp ==> ChgListener{chg=>
            if (chg.oldValue != null) {
                eventHandlers.foreach(chg.oldValue.remAll(_))
                eventHandlers = None
            }
            if (chg.newValue != null) {
                eventHandlers = Some(chg.newValue.bind(actions))
            }
        }
        if (sceneProp.getValue != null) {
            eventHandlers = Some(sceneProp.getValue.bind(actions))
        }
    }

    def findParent[T: ClassTag](node: Node): Option[T] = {
        if (node == null) {
            None
        } else if (node.getClass.isAssignableFrom(implicitly[ClassTag[T]].runtimeClass)) {
            Some(node.asInstanceOf[T])
        } else {
            findParent(node.asInstanceOf[Node].getParent)
        }
    }

    def bindFileChooser(textField: TextField, width: Double, maxHeight: Double,
                        fileChooserType: FileChooserType = FileChooserType.DIRS_AND_FILES)
                       (implicit log: Logger, executor : scala.concurrent.ExecutionContext): Unit = {
        TextFieldFileChooser(textField, width, maxHeight, fileChooserType)
    }

    def bindVarNameAutocomplete(textField: TextField, width: Double, maxHeight: Double,
                        varNameProvider: => List[String])
                       (implicit log: Logger, executor : scala.concurrent.ExecutionContext): Unit = {
        TextFieldVarNameAutocomplete(textField, width, maxHeight, varNameProvider)
    }
}
