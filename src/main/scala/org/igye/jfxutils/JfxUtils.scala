package org.igye.jfxutils

import javafx.event.{EventHandler, EventType, Event}
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.input.KeyEvent
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeLineCap, StrokeLineJoin, StrokeType}

import org.igye.jfxutils.action.{Action, ShortcutActionTrigger}

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

    def bindShortcutActionTrigger(node: Node, actionsList: List[Action]): Unit = {
        val shortcutActionTrigger = new ShortcutActionTrigger(actionsList)
        node.flt(KeyEvent.ANY){e =>
            if (shortcutActionTrigger.hasActionMatchingEvent(e)) {
                if (e.getEventType == KeyEvent.KEY_PRESSED) {
                    shortcutActionTrigger.triggerAction(e)
                }
                e.consume()
            }
        }
    }
}
