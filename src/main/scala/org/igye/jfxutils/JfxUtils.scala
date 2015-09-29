package org.igye.jfxutils

import javafx.event.{EventHandler, EventType, Event}
import javafx.geometry.Insets
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{StrokeLineCap, StrokeLineJoin, StrokeType}

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
}
