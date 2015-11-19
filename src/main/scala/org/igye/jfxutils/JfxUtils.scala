package org.igye.jfxutils

import javafx.event.EventTarget
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.{Tab, TabPane}
import javafx.scene.input.KeyEvent
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeLineCap, StrokeLineJoin, StrokeType}

import org.igye.jfxutils.action.ActionType.{FILTER, HANDLER}
import org.igye.jfxutils.action.{Action, ShortcutActionTrigger}
import org.igye.jfxutils.events.Hnd
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
        createBorder(color, 3)
    }

    def createBackground(fillColor: Color) = {
        new Background(new BackgroundFill(fillColor, CornerRadii.EMPTY, new Insets(0)))
    }

    //todo: make it implicit on Node, pass varargs
    def bindShortcutActionTrigger(node: Node, actionsList: List[Action]): Unit = {
        val filtersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == FILTER))
        node.flt(KeyEvent.ANY){e => filtersShortcutActionTrigger.triggerActionIfNecessary(e)}
        val handlersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == HANDLER))
        node.hnd(KeyEvent.ANY){e => handlersShortcutActionTrigger.triggerActionIfNecessary(e)}
    }

    //todo: make it implicit on Tab, pass varargs
    def bindShortcutActionTrigger(tab: Tab, actionsList: List[Action]): Unit = {
        val filtersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == FILTER))
        val flt = Hnd(KeyEvent.ANY){ e =>
            findParent[TabPane](e.getTarget).foreach {tabPane =>
                if (tabPane.getSelectionModel.getSelectedItem == tab) {
                    filtersShortcutActionTrigger.triggerActionIfNecessary(e)
                }
            }
        }
        val handlersShortcutActionTrigger = new ShortcutActionTrigger(actionsList.filter(_.actionType == HANDLER))
        val hnd = Hnd(KeyEvent.ANY){ e =>
            findParent[TabPane](e.getTarget).foreach {tabPane =>
                if (tabPane.getSelectionModel.getSelectedItem == tab) {
                    handlersShortcutActionTrigger.triggerActionIfNecessary(e)
                }
            }
        }
        if (tab.getTabPane() != null) {
            tab.getTabPane().flt(flt)
            tab.getTabPane().hnd(hnd)
        }
        tab.tabPaneProperty() ==> ChgListener{chg=>
            if (chg.oldValue != null) {
                chg.oldValue.remFlt(flt)
                chg.oldValue.remHnd(hnd)
            }
            if (chg.newValue != null) {
                chg.newValue.flt(flt)
                chg.newValue.hnd(hnd)
            }
        }
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
