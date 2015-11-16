package org.igye.jfxutils

import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.{Event, EventHandler, EventTarget, EventType}
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.{Tab, TabPane}
import javafx.scene.input.KeyEvent
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeLineCap, StrokeLineJoin, StrokeType}

import org.igye.jfxutils.action.{Action, ShortcutActionTrigger}

import scala.reflect.ClassTag

object JfxUtils {
    def createBorder(color: Color) = {
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
                new BorderWidths(3),
                new Insets(0)
            )
        )
    }

    def createBackground(fillColor: Color) = {
        new Background(new BackgroundFill(fillColor, CornerRadii.EMPTY, new Insets(0)))
    }

    def eventHandler[T <: Event](eventType: EventType[T])(hnd: T => Unit) = {
        EventHandlerInfo(
            eventType,
            new EventHandler[T] {
                override def handle(event: T): Unit = {
                    hnd(event)
                }
            }
        )
    }

    //todo: make it implicit on Node, pass varargs
    def bindShortcutActionTrigger(node: Node, actionsList: List[Action]): Unit = {
        val shortcutActionTrigger = new ShortcutActionTrigger(actionsList)
        node.flt(KeyEvent.ANY){e => shortcutActionTrigger.triggerActionIfNecessary(e)}
    }

    //todo: make it implicit on Tab, pass varargs
    def bindShortcutActionTrigger(tab: Tab, actionsList: List[Action]): Unit = {
        val shortcutActionTrigger = new ShortcutActionTrigger(actionsList)
        val hnd = eventHandler(KeyEvent.ANY){e =>
            findParent[TabPane](e.getTarget).foreach {tabPane =>
                if (tabPane.getSelectionModel.getSelectedItem == tab) {
                    shortcutActionTrigger.triggerActionIfNecessary(e)
                }
            }
        }
        if (tab.getTabPane() != null) {
            tab.getTabPane().flt(hnd)
        }
        tab.tabPaneProperty().addListener(ChgListener[TabPane]{ v =>
            if (v.oldValue != null) {
                v.oldValue.remFlt(hnd)
            }
            if (v.newValue != null) {
                v.newValue.flt(hnd)
            }
        })
    }

    def findParent[T: ClassTag](eventTarget: EventTarget): Option[T] = {
        if (eventTarget == null) {
            None
        } else if (eventTarget.getClass.isAssignableFrom(implicitly[ClassTag[T]].runtimeClass)) {
            Some(eventTarget.asInstanceOf[T])
        } else if (eventTarget.isInstanceOf[Node]) {
            findParent(eventTarget.asInstanceOf[Node].getParent)
        } else {
            None
        }
    }
}
